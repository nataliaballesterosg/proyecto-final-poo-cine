#!/usr/bin/env bash
# =====================================================================
# Instala K3s como nodo MASTER (servidor) en vm-app-1.
# Al terminar muestra la IP y el TOKEN necesarios para unir otros nodos.
#
# Uso:  ./infra/instalar-k3s-master.sh
# =====================================================================
set -euo pipefail

# --disable traefik: no usamos Ingress, la API se expone con un Service NodePort
curl -sfL https://get.k3s.io | INSTALL_K3S_VERSION=v1.30.4+k3s1 sh -s - server \
  --disable traefik \
  --write-kubeconfig-mode 644

# kubectl sin sudo para el usuario actual
mkdir -p ~/.kube
cp /etc/rancher/k3s/k3s.yaml ~/.kube/config

echo ">> Esperando a que el nodo este listo..."
until kubectl get nodes | grep -q " Ready"; do sleep 3; done
kubectl get nodes -o wide

echo
echo "================ DATOS PARA UNIR NODOS ================"
echo "IP_MASTER = $(hostname -I | awk '{print $1}')"
echo "TOKEN     = $(sudo cat /var/lib/rancher/k3s/server/node-token)"
echo "En vm-app-2 / vm-app-3 ejecuta:"
echo "  ./infra/unir-nodo-k3s.sh <IP_MASTER> <TOKEN>"
echo "========================================================"
