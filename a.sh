#!/bin/bash

set -e

echo "🧹 Удаление старых версий Docker (если есть)..."

REMOVE_PACKAGES=(
  docker
  docker.io
  docker-doc
  docker-compose
  docker-compose-plugin
  containerd
  runc
)

for pkg in "${REMOVE_PACKAGES[@]}"; do
  if dpkg -l | grep -q "^ii\s*$pkg"; then
    echo " - Удаляем $pkg..."
    sudo apt remove -y "$pkg"
  else
    echo " - $pkg не установлен, пропускаем."
  fi
done

echo "✅ Удаление завершено."

echo "📦 Установка зависимостей..."
sudo apt update
sudo apt install -y ca-certificates curl gnupg

echo "🔐 Добавление GPG-ключа Docker..."
sudo install -m 0755 -d /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | \
  sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg

echo "➕ Добавление репозитория Docker..."
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] \
  https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

echo "🔄 Обновление списка пакетов..."
sudo apt update

echo "🚀 Установка Docker CE и Compose plugin..."
sudo apt install -y \
  docker-ce \
  docker-ce-cli \
  containerd.io \
  docker-buildx-plugin \
  docker-compose-plugin

echo "🔧 Проверка версий..."
docker --version
docker compose version

echo "✅ Docker установлен и готов к работе!"
echo "👉 Попробуйте: sudo docker compose up --build -d"
