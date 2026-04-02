#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

echo "==> Running integration tests..."
"$SCRIPT_DIR/gradlew" intTest \
  --tests "io.atlassian.micros.studio.application.spaces.repository.SpacesRepositoryIT"
