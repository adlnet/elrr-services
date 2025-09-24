#!/bin/bash

set -e

# Start the server in the background
echo "Starting Spring Boot server..."
mvn spring-boot:run -Dspring-boot.run.profiles=local > target/openapi-server.log 2>&1 &
SERVER_PID=$!

# Wait for the server to be up (poll the OpenAPI endpoint)
OPENAPI_URL="http://localhost:8092/v3/api-docs"
TIMEOUT=60
ELAPSED=0
SLEEP=2

until curl -sf "$OPENAPI_URL" > /dev/null; do
  if [ $ELAPSED -ge $TIMEOUT ]; then
    echo "Server did not start within $TIMEOUT seconds."
    kill $SERVER_PID
    exit 1
  fi
  sleep $SLEEP
  ELAPSED=$((ELAPSED+SLEEP))
  echo "Waiting for server... ($ELAPSED/$TIMEOUT)"
done

echo "Server is up. Fetching OpenAPI JSON..."
curl -s "$OPENAPI_URL" -o docs/api/openapi.json

# Stop the server
echo "Stopping server (PID $SERVER_PID)..."
kill $SERVER_PID
wait $SERVER_PID 2>/dev/null || true

echo "OpenAPI JSON saved to docs/api/openapi.json."
