#!/usr/bin/env bash
# =====================================================================
# Despliega la aplicacion completa en Kubernetes.
# Se ejecuta en el nodo master (donde esta kubectl).
#
# Uso:
#   IMAGEN=miusuario/cine-api:1.0 ./k8s/desplegar.sh
# =====================================================================
set -euo pipefail
cd "$(dirname "$0")/.."

IMAGEN=${IMAGEN:?"Define la imagen, ej: IMAGEN=miusuario/cine-api:1.0 ./k8s/desplegar.sh"}
NODO_DB=${NODO_DB:-$(kubectl get nodes -l node-role.kubernetes.io/control-plane -o jsonpath='{.items[0].metadata.name}')}

echo ">> Etiquetando el nodo '$NODO_DB' como nodo de base de datos"
kubectl label node "$NODO_DB" rol=base-datos --overwrite

kubectl apply -f k8s/00-namespace.yaml
kubectl apply -f k8s/01-secret.yaml

echo ">> Creando ConfigMap con el script de datos (db/init.sql)"
kubectl create configmap db-init -n cine --from-file=init.sql=db/init.sql \
  --dry-run=client -o yaml | kubectl apply -f -

kubectl apply -f k8s/02-postgres.yaml
echo ">> Esperando a PostgreSQL (la primera vez carga 11.210 registros, unos segundos)..."
kubectl rollout status deployment/basedatos -n cine --timeout=300s

sed "s|TU_USUARIO/cine-api:1.0|$IMAGEN|" k8s/03-api.yaml | kubectl apply -f -
kubectl rollout status deployment/cine-api -n cine --timeout=300s

kubectl get pods -n cine -o wide
echo ">> Listo. Prueba: curl http://<IP_DE_CUALQUIER_NODO>:30080/api/info"
