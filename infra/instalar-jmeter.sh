#!/usr/bin/env bash
# =====================================================================
# Instala Java 17 y Apache JMeter 5.6.3 en vm-jmeter.
# Uso:  ./infra/instalar-jmeter.sh   (luego:  source ~/.bashrc)
# =====================================================================
set -euo pipefail
sudo apt-get update -y
sudo apt-get install -y openjdk-17-jre-headless curl

VERSION=5.6.3
cd ~
curl -fsSL "https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-$VERSION.tgz" | tar xz
echo "export PATH=\$HOME/apache-jmeter-$VERSION/bin:\$PATH" >> ~/.bashrc

# Mas memoria para JMeter (200 usuarios concurrentes)
sed -i 's/-Xms1g -Xmx1g/-Xms1g -Xmx2g/' "apache-jmeter-$VERSION/bin/jmeter" || true

echo ">> JMeter instalado. Ejecuta:  source ~/.bashrc  &&  jmeter -v"
