# Manual API Performance Report

**Environment**: Tomcat + Java Servlets  
**Test Date**: 28.11.2025, 00:08:36  
**Iterations**: 10  

## Response Time Statistics (ms)

| API Endpoint | Min | Max | Average | Median | Std Dev |
|-------------|-----|-----|---------|--------|----------|

### Users

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| GET All Users | 41 | 345 | 98 | 49 | 92 |
| POST Create User | 49 | 176 | 68 | 53 | 37 |
| GET User by ID | 40 | 144 | 54 | 46 | 30 |
| PUT Update User | 81 | 205 | 114 | 90 | 46 |
| GET Functions by User ID | 38 | 48 | 43 | 43 | 4 |
| DELETE User | 48 | 57 | 53 | 54 | 3 |

### Functions

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| POST Create Function | 83 | 214 | 99 | 85 | 39 |
| GET All Functions | 74 | 178 | 90 | 77 | 31 |
| GET Function by ID | 38 | 50 | 41 | 41 | 3 |

### Other

| Endpoint | Min | Max | Avg | Median | Std Dev |
|----------|-----|-----|-----|--------|----------|
| 🏠 Health Check | 1 | 55 | 8 | 3 | 16 |

## Summary

| Metric | Value |
|--------|-------|
| Total Requests Tested | 10 |
| Average Response Time | 67 ms |
| Fastest Endpoint | 🏠 Health Check (8 ms) |
| Slowest Endpoint | PUT Update User (114 ms) |
