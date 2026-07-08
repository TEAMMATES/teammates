#!/bin/bash

attempt=0
while [ $attempt -le 60 ]; do
  attempt=$(($attempt+1))
  echo "Waiting for server to be up (attempt: $attempt)..."
  result=$(curl -svo /dev/null http://localhost:8080/ping 2>&1 | grep "200 OK")
  if grep -q "200 OK" <<< $result; then
    echo "Server is up!"
    break
  fi
  sleep 1
done
