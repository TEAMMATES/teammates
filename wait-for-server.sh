#!/bin/bash

port="${1:-8080}"
path="${2:-}"

if [ -z "$path" ] && [ "$port" = "8080" ]; then
  path="/ping"
fi

attempt=0
while [ $attempt -le 60 ]; do
  attempt=$(($attempt+1))
  echo "Waiting for server on port $port to be up (attempt: $attempt)..."
  result=$(curl -svo /dev/null "http://localhost:$port$path" 2>&1 | grep "200 OK")
  if grep -q "200 OK" <<< $result; then
    echo "Server is up!"
    exit 0
  fi
  sleep 1
done

echo "Server on port $port did not start in time."
exit 1
