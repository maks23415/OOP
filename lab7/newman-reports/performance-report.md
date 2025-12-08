# Manual API Performance Report

**Environment**: Tomcat + Java Servlets  
**Test Date**: 08.12.2025, 16:18:46  
**Iterations**: 10  

## Response Time Statistics (ms)

| API Endpoint | Min | Max | Average | Median | Std Dev |
|-------------|-----|-----|---------|--------|----------|

### Users

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create User | 47 | 159 | 61 | 50 | 33 |
| GET All Users | 2 | 5 | 3 | 3 | 1 |
| GET User by ID | 2 | 5 | 3 | 3 | 1 |
| PUT Update User | 2 | 7 | 5 | 5 | 2 |
| DELETE User | 2 | 18 | 5 | 4 | 5 |
| GET Functions by User ID | 2 | 5 | 3 | 4 | 1 |

### Functions

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create Function | 1 | 4 | 3 | 3 | 1 |
| GET All Functions | 2 | 5 | 3 | 2 | 1 |
| GET Function by ID | 1 | 6 | 4 | 4 | 1 |

### Other

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| Health Check | 2 | 18 | 5 | 4 | 5 |

## Summary

| Metric | Value |
|--------|-------|
| Total Requests Tested | 10 |
| Average Response Time | 10 ms |
| Fastest Endpoint | GET All Users (3 ms) |
| Slowest Endpoint | POST Create User (61 ms) |
