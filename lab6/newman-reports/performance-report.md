# Manual API Performance Report

**Environment**: Tomcat + Java Servlets  
**Test Date**: 09.12.2025, 18:12:40  
**Iterations**: 10  

## Response Time Statistics (ms)

| API Endpoint | Min | Max | Average | Median | Std Dev |
|-------------|-----|-----|---------|--------|----------|

### Users

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create User | 42 | 57 | 46 | 45 | 4 |
| GET All Users | 2 | 6 | 4 | 4 | 1 |
| GET User by ID | 1 | 5 | 2 | 2 | 1 |
| PUT Update User | 2 | 6 | 3 | 2 | 1 |
| DELETE User | 1 | 6 | 3 | 3 | 1 |
| GET Functions by User ID | 2 | 5 | 4 | 4 | 1 |

### Functions

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create Function | 1 | 10 | 3 | 3 | 2 |
| GET All Functions | 1 | 5 | 4 | 4 | 1 |
| GET Function by ID | 2 | 4 | 3 | 3 | 1 |

### Other

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| Health Check | 1 | 19 | 4 | 2 | 5 |

## Summary

| Metric | Value |
|--------|-------|
| Total Requests Tested | 10 |
| Average Response Time | 8 ms |
| Fastest Endpoint | GET User by ID (2 ms) |
| Slowest Endpoint | POST Create User (46 ms) |
