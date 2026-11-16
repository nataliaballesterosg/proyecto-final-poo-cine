#!/usr/bin/env bash
# =====================================================================
# Cambia el numero de replicas de la API y las redistribuye entre nodos.
#
# Uso:
#   ./k8s/escalar.sh 3
# =====================================================================
set -euo pipefail
REPLICAS=${1:?"Uso: $0 <numero_de_replicas>"}

kubectl scale deployment cine-api -n cine --replicas="$REPLICAS"

# Reinicia los pods para que el scheduler los reparta de nuevo entre TODOS
# los nodos actuales (importante despues de agregar un nodo al cluster).
kubectl rollout restart deployment cine-api -n cine
kubectl rollout status deployment cine-api -n cine --timeout=300s

# Espera a que desaparezcan los pods viejos (estado Terminating)
until [ "$(kubectl get pods -n cine -l app=cine-api --no-headers | wc -l)" -eq "$REPLICAS" ]; do
  sleep 2
done

echo ">> Distribucion de pods por nodo:"
kubectl get pods -n cine -o wide
