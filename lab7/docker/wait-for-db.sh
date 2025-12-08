#!/bin/bash

# Wait for PostgreSQL to be ready
host="$1"
port="$2"
user="$3"
pass="$4"
db="$5"

echo "Waiting for PostgreSQL to be ready..."
until PGPASSWORD="$pass" psql -h "$host" -p "$port" -U "$user" -d "$db" -c '\q'; do
  echo "PostgreSQL is unavailable - sleeping"
  sleep 2
done

echo "PostgreSQL is ready - starting application"