#!/usr/bin/env bash
set -euo pipefail

COMPOSE_FILE="test-infrastructure/docker/compose.dependencies-only-mysql.yml"

if [[ "${1:-}" == "stop" ]]; then
    docker compose -f "$COMPOSE_FILE" down
    exit 0
fi

docker compose -f "$COMPOSE_FILE" up -d
