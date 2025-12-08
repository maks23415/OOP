#!/bin/bash

echo "Building Lab5 Manual Application..."

# Clean and build project
mvn clean package

if [ $? -ne 0 ]; then
    echo "❌ Maven build failed!"
    exit 1
fi

echo "✅ Build successful"

# Build Docker image
echo "Building Docker image..."
docker-compose build

if [ $? -ne 0 ]; then
    echo "❌ Docker build failed!"
    exit 1
fi

echo "✅ Docker image built successfully"
echo "🚀 To start the application, run: docker-compose up -d"