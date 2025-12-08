#!/bin/bash

echo "Starting Lab5 Manual Application..."

# Stop existing containers
docker-compose down

# Start services
docker-compose up -d

echo "Waiting for services to start..."
sleep 30

# Check services status
echo "Checking services status..."
docker-compose ps

# Test application
echo "Testing application..."
curl -f http://localhost:8080/ > /dev/null 2>&1

if [ $? -eq 0 ]; then
    echo "✅ Application is running at http://localhost:8080"
    echo "📊 PostgreSQL is running at localhost:5432"
    echo "📝 Logs: docker-compose logs -f lab5-app"
else
    echo "❌ Application failed to start"
    docker-compose logs lab5-app
    exit 1
fi