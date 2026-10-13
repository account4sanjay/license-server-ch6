#!/usr/bin/env bash
# Stop and remove the stack's containers and network. Images are kept.
set -euo pipefail
cd "$(dirname "$0")/docker"

if command -v podman >/dev/null 2>&1; then ENGINE=podman; else ENGINE=docker; fi

echo "==> Stopping containers"
$ENGINE compose down
