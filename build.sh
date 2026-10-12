#!/usr/bin/env bash
# Build all services and their container images with Podman.
# Usage: ./build.sh            -> mvn clean package docker:build
#        ./build.sh <goals...> -> mvn <goals...>  (e.g. ./build.sh clean package)
set -euo pipefail
cd "$(dirname "$0")"

# Start the Podman VM if it is not already running.
if ! podman machine inspect --format '{{.State}}' | grep -q running; then
  echo "Starting Podman machine..."
  podman machine start
fi

export DOCKER_HOST="unix://$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}')"
echo "DOCKER_HOST=$DOCKER_HOST"

if [ $# -eq 0 ]; then
  set -- clean package docker:build
fi
exec mvn "$@"
