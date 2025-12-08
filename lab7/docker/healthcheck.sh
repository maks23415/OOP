#!/bin/bash

# Health check for Tomcat application
URL="http://localhost:8080/"

# Check if Tomcat is responding
response=$(curl -s -o /dev/null -w "%{http_code}" "$URL")

if [ "$response" -eq 200 ]; then
    # Additional check for API endpoints
    api_response=$(curl -s -o /dev/null -w "%{http_code}" -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" "http://localhost:8080/users")

    if [ "$api_response" -eq 200 ] || [ "$api_response" -eq 401 ] || [ "$api_response" -eq 403 ]; then
        exit 0
    else
        exit 1
    fi
else
    exit 1
fi