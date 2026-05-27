#!/usr/bin/env bash
set -euo pipefail

APP_DIR="${APP_DIR:-/home/ubuntu/app}"
PID_FILE="${PID_FILE:-${APP_DIR}/app.pid}"

PIDS=""

if [[ -f "${PID_FILE}" ]]; then
  PID="$(cat "${PID_FILE}")"
  if [[ -n "${PID}" ]] && kill -0 "${PID}" 2>/dev/null; then
    PIDS="${PID}"
  fi
fi

if [[ -z "${PIDS}" ]]; then
  PIDS="$(pgrep -f "java .*${APP_DIR}/kinoton.jar" || true)"
fi

if [[ -z "${PIDS}" ]]; then
  rm -f "${PID_FILE}"
  echo "Application is not running."
  exit 0
fi

kill ${PIDS} 2>/dev/null || true

for _ in $(seq 1 30); do
  RUNNING=""
  for PID in ${PIDS}; do
    if kill -0 "${PID}" 2>/dev/null; then
      RUNNING="true"
    fi
  done

  if [[ -z "${RUNNING}" ]]; then
    rm -f "${PID_FILE}"
    echo "Application stopped."
    exit 0
  fi

  sleep 1
done

kill -9 ${PIDS} 2>/dev/null || true
rm -f "${PID_FILE}"
echo "Application force stopped."
