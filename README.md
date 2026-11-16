# Proyecto Final — Comportamiento de una aplicación bajo diferentes escenarios de despliegue

**Curso:** Principios y prácticas de Desarrollo de Software Orientado a Objetos
**Integrantes:** Laura Ballesteros · Nikolas Pedrozo · Gabriel Garcia

API REST de **reservas de un cine** (Spring Boot + PostgreSQL, 11.210 registros) desplegada con
**Docker Compose** y con **Kubernetes (K3s)** en 1, 2 y 3 nodos con 1, 2 y 3 réplicas.
La carga se genera con **JMeter** y los resultados se analizan en un **Notebook de Jupyter**.

| Entregable | Ubicación |
|---|---|
| Código fuente + Dockerfile | [`app/`](app/) |
| Docker Compose | [`docker-compose.yml`](docker-compose.yml) |
| Manifiestos de Kubernetes | [`k8s/`](k8s/) |
| Plan de JMeter y script de pruebas | [`jmeter/`](jmeter/) |
| Resultados crudos de JMeter | [`resultados/`](resultados/) |
| Notebook de análisis | [`notebook/Analisis_Despliegues.ipynb`](notebook/Analisis_Despliegues.ipynb) |

---

## 1. La aplicación

| Tabla | Registros | Descripción |
|---|---|---|
| `peliculas` | 200 | título, género, duración, clasificación, año |
| `salas` | 10 | nombre, filas (A, B, C…) y sillas por fila |
| `funciones` | 1.000 | película + sala + fecha/hora + precio |
| `reservas` | 10.000 | una silla (fila, número) de una función para un cliente |

**Regla de negocio principal:** una silla no se puede reservar dos veces en la misma función.
Se valida en el servicio y además la garantiza la base de datos con una restricción
`UNIQUE (funcion_id, fila, numero)`, así funciona aunque haya varias réplicas atendiendo a la vez.

## 2. Estructura del repositorio

```
.
├── app/                    API REST (Java 17, Spring Boot 3)
│   ├── Dockerfile          imagen multi-etapa (Maven -> JRE)
│   └── src/main/java/com/proyecto/cine/
│       ├── modelo/         entidades JPA: Pelicula, Sala, Funcion, Reserva
│       ├── dto/            objetos de entrada/salida de la API
│       ├── repositorio/    acceso a datos (Spring Data JPA)
│       ├── servicio/       lógica de negocio
│       ├── controlador/    endpoints REST
│       └── excepcion/      manejo de errores HTTP (404, 409, 400)
├── db/init.sql             esquema + 200 películas, 10 salas, 1.000 funciones, 10.000 reservas
├── docker-compose.yml      Fase 1
├── k8s/                    Fases 2, 3 y 4 (manifiestos + desplegar.sh + escalar.sh)
├── infra/                  scripts para instalar Docker, K3s y JMeter
├── jmeter/                 plan-cine.jmx + run-pruebas.sh
├── resultados/             un directorio por escenario con los .csv.gz de JMeter
└── notebook/               análisis en Jupyter
```

## 3. Arquitectura de pruebas

```
  ┌────────────┐   HTTP   ┌──────────────────────────────────────────────┐
  │ vm-jmeter  │ ───────► │ vm-app-1 (master)   vm-app-2      vm-app-3   │
  │  JMeter    │  :30080  │  PostgreSQL          API (pods)   API (pods) │
  └────────────┘          │  API (pods)                                  │
                          └──────────────────────────────────────────────┘
```

* 4 VMs Ubuntu 22.04 de **2 vCPU** creadas con Multipass (ver sección 7).
* JMeter **siempre** corre en una máquina distinta a la aplicación.
* Cada contenedor de la API está limitado a **1 CPU / 768 MB** en todos los escenarios (comparación justa).
* PostgreSQL es una sola instancia en el nodo master; lo que se escala es la API.

## 4. Paso a paso

> Clona el repositorio en **todas** las VMs:
> `git clone https://github.com/nataliaballesterosg/proyecto-final-poo-cine.git && cd proyecto-final-poo-cine`

### 4.0 Crear las máquinas
```bash
multipass launch 22.04 --name vm-app-1  --cpus 2 --memory 3G --disk 12G
multipass launch 22.04 --name vm-app-2  --cpus 2 --memory 2G --disk 10G
multipass launch 22.04 --name vm-app-3  --cpus 2 --memory 2G --disk 10G
multipass launch 22.04 --name vm-jmeter --cpus 2 --memory 2G --disk 8G
multipass list
```
Anota la **IP** de `vm-app-1` (la llamaremos `IP_APP1`).

### 4.1 Fase 1 — Docker Compose (en `vm-app-1`)
```bash
./infra/instalar-docker.sh            # luego salir y volver a entrar por SSH
docker compose up -d --build
curl http://localhost:8080/api/funciones/15/sillas
```
En `vm-jmeter`:
```bash
./infra/instalar-jmeter.sh && source ~/.bashrc
./jmeter/run-pruebas.sh compose IP_APP1 8080
```
Al terminar, en `vm-app-1`: `docker compose down -v`

### 4.2 Copiar la imagen a los nodos
Como las VMs son locales, la imagen no se publica en un registro: se exporta en `vm-app-1` y se copia a
las demás VMs desde el equipo anfitrión.
```bash
sudo docker save cine-api:1.0 -o ~/cine-api.tar                          # en vm-app-1
multipass transfer vm-app-1:/home/ubuntu/cine-api.tar ./cine-api.tar     # en el equipo anfitrión
multipass transfer ./cine-api.tar vm-app-2:/home/ubuntu/cine-api.tar
multipass transfer ./cine-api.tar vm-app-3:/home/ubuntu/cine-api.tar
```

### 4.3 Fase 2 — Kubernetes con 1 nodo (en `vm-app-1`)
```bash
./infra/instalar-k3s-master.sh        # muestra IP_MASTER y TOKEN: guárdalos
sudo k3s ctr images import ~/cine-api.tar
IMAGEN=docker.io/library/cine-api:1.0 ./k8s/desplegar.sh
```
Para cada número de réplicas R = 1, 2, 3:
```bash
./k8s/escalar.sh R                                  # en vm-app-1 (verifica que todos los pods queden Running)
./jmeter/run-pruebas.sh k8s-N1-RR IP_APP1 30080     # en vm-jmeter  (ej: k8s-N1-R2)
```

### 4.4 Fase 3 — Kubernetes con 2 nodos
```bash
./infra/unir-nodo-k3s.sh IP_MASTER TOKEN            # en vm-app-2
sudo k3s ctr images import ~/cine-api.tar           # en vm-app-2
kubectl get nodes                                   # en vm-app-1: deben verse 2 nodos Ready
```
Repite el ciclo de réplicas usando los nombres `k8s-N2-R1`, `k8s-N2-R2`, `k8s-N2-R3`.

### 4.5 Fase 4 — Kubernetes con 3 nodos
```bash
./infra/unir-nodo-k3s.sh IP_MASTER TOKEN            # en vm-app-3
sudo k3s ctr images import ~/cine-api.tar           # en vm-app-3
```
Repite el ciclo con `k8s-N3-R1`, `k8s-N3-R2`, `k8s-N3-R3`.

> Guarda la distribución de pods de cada escenario para el análisis:
> `kubectl get pods -n cine -o wide > resultados/k8s-N2-R3/pods.txt`

### 4.6 Subir resultados y analizar
```bash
git add resultados && git commit -m "Resultados de las pruebas" && git push
```
Abre el notebook en Colab y ejecuta **Entorno de ejecución → Ejecutar todas**:
<https://colab.research.google.com/github/nataliaballesterosg/proyecto-final-poo-cine/blob/main/notebook/Analisis_Despliegues.ipynb>

## 5. Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/peliculas?pagina=0&tamanio=20` | cartelera paginada |
| GET | `/api/peliculas/{id}` | detalle de una película |
| GET | `/api/peliculas/buscar?titulo=leyenda` | búsqueda por título |
| POST · PUT · DELETE | `/api/peliculas` · `/api/peliculas/{id}` | crear, actualizar, eliminar |
| GET | `/api/peliculas/{id}/funciones` | funciones de una película |
| GET | `/api/funciones/{id}` | detalle de una función |
| GET | `/api/funciones/{id}/sillas` | **mapa de sillas** (`#` ocupada, `.` libre) |
| POST | `/api/reservas` | reservar `{"funcionId":15,"fila":2,"numero":3,"cliente":"ana"}` → 201 / 409 |
| GET · DELETE | `/api/reservas/{id}` | consultar · cancelar una reserva |
| GET | `/api/estadisticas/ocupacion` | % de ocupación de cada película (consulta pesada) |
| GET | `/api/info` | pod y nodo que atendió la petición |
| GET | `/actuator/health` | salud (usada por las *probes*) |

## 6. Ejecución local rápida (sin nube)

Con Docker instalado en tu computador:
```bash
docker compose up -d --build
curl http://localhost:8080/api/funciones/15/sillas
```

## 7. Nota sobre los resultados incluidos

Los resultados de la carpeta `resultados/` se obtuvieron siguiendo la sección 4 en **4 VMs Multipass
(Hyper-V) sobre un mismo portátil**: `vm-app-1` con 2 vCPU / 3 GB, y `vm-app-2`, `vm-app-3` y `vm-jmeter`
con 2 vCPU / 2 GB (`vm-app-2` y `vm-app-3` se redujeron de 3 GB a 2 GB por la memoria disponible del equipo).
