# Manual API Performance Report

**Environment**: Tomcat + Java Servlets  
**Test Date**: 04.12.2025, 00:08:44  
**Iterations**: 10  

## Response Time Statistics (ms)

| API Endpoint | Min | Max | Average | Median | Std Dev |
|-------------|-----|-----|---------|--------|----------|

### Users

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create User | 65 | 179 | 86 | 72 | 34 |
| GET All Users | 4 | 12 | 5 | 4 | 2 |
| GET User by ID | 3 | 8 | 4 | 4 | 1 |
| PUT Update User | 2 | 6 | 4 | 4 | 1 |
| DELETE User | 2 | 12 | 5 | 3 | 3 |
| GET Functions by User ID | 2 | 5 | 4 | 4 | 1 |

### Functions

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create Function | 2 | 15 | 5 | 4 | 4 |
| GET All Functions | 4 | 6 | 4 | 4 | 1 |
| GET Function by ID | 2 | 5 | 4 | 4 | 1 |

### Other

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| 🏠 Health Check | 2 | 38 | 7 | 4 | 11 |

## Summary

| Metric | Value |
|--------|-------|
| Total Requests Tested | 10 |
| Average Response Time | 13 ms |
| Fastest Endpoint | GET User by ID (4 ms) |
| Slowest Endpoint | POST Create User (86 ms) |
