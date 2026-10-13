#!/usr/bin/env bash
# Build the services and their container images, then start the whole stack.
#
# Usage: ./start.sh              build (mvn clean package docker:build) and start
#        ./start.sh --no-build   start the containers from the existing images
#        ./start.sh --skip-tests build without running the unit tests, then start
set -euo pipefail
cd "$(dirname "$0")"

BUILD=true
MVN_ARGS=()
for arg in "$@"; do
  case "$arg" in
    --no-build)   BUILD=false ;;
    --skip-tests) MVN_ARGS+=(-DskipTests) ;;
    *) echo "Unknown option: $arg" >&2; exit 2 ;;
  esac
done

# Use Podman when it is installed, otherwise Docker.
if command -v podman >/dev/null 2>&1; then
  ENGINE=podman
  if ! podman machine inspect --format '{{.State}}' 2>/dev/null | grep -q running; then
    echo "Starting Podman machine..."
    podman machine start
  fi
  export DOCKER_HOST="unix://$(podman machine inspect --format '{{.ConnectionInfo.PodmanSocket.Path}}')"
else
  ENGINE=docker
fi

if $BUILD; then
  echo "==> Building services and images"
  mvn clean package docker:build ${MVN_ARGS[@]+"${MVN_ARGS[@]}"}
fi

echo "==> Starting containers"
cd docker
$ENGINE compose up -d

echo
$ENGINE ps --format '{{.Names}}\t{{.Status}}\t{{.Ports}}' | grep docker_
cat <<MSG

Config server   http://localhost:8071
Eureka peers    http://localhost:8070  http://localhost:8072  http://localhost:8073
Licensing       http://localhost:8080
Organization    http://localhost:8081

Services take about a minute to register with Eureka. Follow the logs with:
  (cd docker && $ENGINE compose logs -f)
Stop everything with ./stop.sh
MSG
