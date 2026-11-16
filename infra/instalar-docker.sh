#!/usr/bin/env bash
# =====================================================================
# Instala Docker y Docker Compose en Ubuntu (se usa en vm-app-1).
# Uso:  ./infra/instalar-docker.sh   (luego cierra y abre la sesion SSH)
# =====================================================================
set -euo pipefail
curl -fsSL https://get.docker.com | sudo sh
sudo usermod -aG docker "$USER"
echo ">> Docker instalado. Cierra la sesion SSH y vuelve a entrar para usar 'docker' sin sudo."
