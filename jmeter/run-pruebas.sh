#!/usr/bin/env bash
# =====================================================================
# Ejecuta la bateria de pruebas de carga de UN escenario con JMeter.
# Se corre en la VM de JMeter (distinta a las VMs de la aplicacion).
#
# Uso:
#   ./run-pruebas.sh <escenario> <ip> <puerto>
#
# Nombres de escenario (el notebook los reconoce asi):
#   compose          -> Docker Compose (Fase 1)
#   k8s-N1-R1 ...    -> Kubernetes con N nodos y R replicas (Fases 2, 3 y 4)
#
# Ejemplos:
#   ./run-pruebas.sh compose    10.128.0.2 8080
#   ./run-pruebas.sh k8s-N2-R3  10.128.0.2 30080
#
# Variables opcionales (con sus valores por defecto):
#   USUARIOS="10 50 100 200"  niveles de carga (usuarios concurrentes)
#   DURACION=120             segundos que dura cada nivel de carga
#   RAMPA=20                 segundos para llegar al total de usuarios
#   PAUSA=100                ms de pausa entre peticiones de un usuario
#   DESCANSO=30              segundos de reposo entre niveles de carga
# =====================================================================
set -euo pipefail

if [ $# -lt 3 ]; then
  echo "Uso: $0 <escenario> <ip> <puerto>"; exit 1
fi

ESCENARIO=$1
HOST=$2
PUERTO=$3
USUARIOS=${USUARIOS:-"10 50 100 200"}
DURACION=${DURACION:-120}
RAMPA=${RAMPA:-20}
PAUSA=${PAUSA:-100}
DESCANSO=${DESCANSO:-30}
JMETER=${JMETER:-jmeter}

DIR_JMETER="$(cd "$(dirname "$0")" && pwd)"
DIR_SALIDA="$DIR_JMETER/../resultados/$ESCENARIO"
mkdir -p "$DIR_SALIDA"

echo ">> Verificando que la API responde en http://$HOST:$PUERTO ..."
curl -sf "http://$HOST:$PUERTO/api/info" > /dev/null || { echo "La API no responde"; exit 1; }

# Calentamiento: la JVM compila el codigo "caliente" (JIT) y se llenan las cachés.
# Estos resultados NO se guardan.
echo ">> Calentamiento (30 s, 10 usuarios)..."
"$JMETER" -n -t "$DIR_JMETER/plan-cine.jmx" -q "$DIR_JMETER/resultados.properties" \
  -Jhost="$HOST" -Jpuerto="$PUERTO" -Jusuarios=10 -Jrampa=5 -Jduracion=30 -Jpausa="$PAUSA" \
  -l /tmp/calentamiento.csv > /dev/null
rm -f /tmp/calentamiento.csv
sleep 10

# Datos del escenario, para dejar constancia en el repositorio
cat > "$DIR_SALIDA/info.txt" <<EOF
escenario=$ESCENARIO
host=$HOST
puerto=$PUERTO
usuarios=$USUARIOS
duracion_s=$DURACION
rampa_s=$RAMPA
pausa_ms=$PAUSA
fecha=$(date -Iseconds)
EOF

for U in $USUARIOS; do
  ARCHIVO="$DIR_SALIDA/usuarios_${U}.csv"
  echo ">> [$ESCENARIO] $U usuarios durante ${DURACION}s ..."
  "$JMETER" -n -t "$DIR_JMETER/plan-cine.jmx" -q "$DIR_JMETER/resultados.properties" \
    -Jhost="$HOST" -Jpuerto="$PUERTO" -Jusuarios="$U" -Jrampa="$RAMPA" \
    -Jduracion="$DURACION" -Jpausa="$PAUSA" -l "$ARCHIVO" | grep -E "summary =" | tail -1
  gzip -f "$ARCHIVO"     # comprimido para que el repositorio no pese demasiado
  echo ">> Descanso de ${DESCANSO}s"
  sleep "$DESCANSO"
done

echo ">> Listo. Resultados en resultados/$ESCENARIO/"
