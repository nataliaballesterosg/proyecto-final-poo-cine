#!/usr/bin/env bash
# =====================================================================
# Une esta VM al cluster K3s como nodo trabajador (agent).
# Se ejecuta en vm-app-2 (Fase 3) y en vm-app-3 (Fase 4).
#
# Uso:  ./infra/unir-nodo-k3s.sh <IP_MASTER> <TOKEN>
# =====================================================================
set -euo pipefail
IP_MASTER=${1:?"Uso: $0 <IP_MASTER> <TOKEN>"}
TOKEN=${2:?"Uso: $0 <IP_MASTER> <TOKEN>"}

curl -sfL https://get.k3s.io | INSTALL_K3S_VERSION=v1.30.4+k3s1 \
  K3S_URL="https://$IP_MASTER:6443" K3S_TOKEN="$TOKEN" sh -s - agent

echo ">> Nodo unido. Verificalo desde el master con: kubectl get nodes"
