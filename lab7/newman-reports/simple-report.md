# API Test Failure Report
Generated: 03.12.2025, 23:09:37

## Executive Summary
**Status**: ❌ ALL TESTS FAILED
**Primary Issue**: 401 Unauthorized on all requests
**Total Failures**: 162

## Problem Diagnosis
The API is rejecting all requests due to missing or invalid authentication.

### Affected Endpoints:
- GET All Users
- GET User by ID
- PUT Update User
- POST Create Function
- GET All Functions
- GET Function by ID
- GET Functions by User ID
- DELETE User

### Root Cause:
The Postman collection does not include authentication headers.
All API endpoints require authentication but no Authorization header is being sent.

## Quick Fix Instructions

### 1. Check API Authentication Requirements
```bash
# Test if API needs auth
curl -v http://localhost:8080/lab5/users

# Test with basic auth
curl -v -u testuser:testpass http://localhost:8080/lab5/users
```

### 2. Add Authentication to Postman

**Option A: Basic Authentication**
1. Open collection in Postman
2. Add this pre-request script to the collection:
```javascript
const username = pm.environment.get("username");
const password = pm.environment.get("password");
const base64Credentials = btoa(username + ":" + password);
pm.request.headers.add({
    key: "Authorization",
    value: "Basic " + base64Credentials
});
```

**Option B: Bearer Token**
1. First, get a token:
```bash
curl -X POST http://localhost:8080/lab5/login \
     -H "Content-Type: application/json" \
     -d '{"username":"test","password":"test"}'
```
2. Add to Postman environment: `authToken`
3. Add header: `Authorization: Bearer {{authToken}}`

## Test Results Details
- Total iterations: 10
- Total requests: 100
- All requests failed with: HTTP 401 Unauthorized
- Error message: "expected response to have status code 200 but got 401"

## Next Steps
1. Determine correct authentication method for your API
2. Update Postman collection with auth headers
3. Set correct credentials in environment
4. Re-run the tests

## Environment Configuration
```json
{
  "baseUrl": "http://localhost:8080/lab5",
  "username": "testuser",
  "password": "testpass",
  "authToken": ""
}
```

**Note**: Update username/password with actual API credentials.