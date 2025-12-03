# FIXING 401 UNAUTHORIZED ERRORS

## Problem
All API requests are returning HTTP 401 (Unauthorized).

## Immediate Solutions

### Option 1: Add Authentication to Postman Collection

1. **Open the collection in Postman app**
   - File → Import → Select 'lab5-manual.postman_collection.json'

2. **Add a login request (if exists):**
   - Create new POST request to: `{{baseUrl}}/login`
   - Body: `{"username":"testuser","password":"testpass"}`
   - Tests: Extract token from response
     ```javascript
     if (pm.response.code === 200) {
         const jsonData = pm.response.json();
         pm.environment.set("authToken", jsonData.token);
     }
     ```

3. **Add Authorization header to all requests:**
   - Go to each request in collection
   - Headers tab → Add new header:
     - Key: `Authorization`
     - Value: `Bearer {{authToken}}`

4. **Run collection in correct order:**
   - First: Login request
   - Then: All other requests

### Option 2: If API doesn't require authentication

1. **Check server configuration:**
   - Verify CORS settings
   - Check if endpoints are protected by default

2. **Test directly with curl:**
   ```bash
   # Test without auth
   curl -v http://localhost:8080/lab5/users

   # Test with basic auth
   curl -v -u testuser:testpass http://localhost:8080/lab5/users

   # Test POST with JSON
   curl -v -X POST http://localhost:8080/lab5/login \
        -H "Content-Type: application/json" \
        -d '{"username":"test","password":"test"}'
   ```

### Option 3: Modify JavaScript Test Script

Add this pre-request script to your Newman configuration:

```javascript
// In your Postman collection, add this as a pre-request script
const base64Credentials = btoa(pm.environment.get("username") + ":" + pm.environment.get("password"));
pm.request.headers.add({
    key: "Authorization",
    value: "Basic " + base64Credentials
});
```

Or modify the test runner to add headers:

```javascript
// Add this to your Newman config
const config = {
    collection: require('./lab5-manual.postman_collection.json'),
    environment: require('./lab5-manual.postman_environment.json'),
    globals: {
        "id": "globals",
        "name": "Globals",
        "values": [
            {
                "key": "authHeader",
                "value": "Basic " + Buffer.from("testuser:testpass").toString("base64"),
                "enabled": true
            }
        ]
    }
};
```

## Testing Steps

1. **First, verify server is running:**
   ```bash
   curl -I http://localhost:8080
   ```

2. **Check API base endpoint:**
   ```bash
   curl -v http://localhost:8080/lab5
   ```

3. **Test authentication endpoint (if exists):**
   ```bash
   curl -X POST http://localhost:8080/lab5/auth/login \
        -H "Content-Type: application/json" \
        -d '{"username":"admin","password":"admin"}'
   ```

4. **Use the token:**
   ```bash
   curl -H "Authorization: Bearer YOUR_TOKEN" \
        http://localhost:8080/lab5/users
   ```

## Common Authentication Methods

1. **Bearer Token (JWT):**
   - Header: `Authorization: Bearer <token>`
   - Usually from /login endpoint

2. **Basic Authentication:**
   - Header: `Authorization: Basic <base64(username:password)>`
   - Encode: `btoa('username:password')`

3. **API Key:**
   - Header: `X-API-Key: <key>`
   - Or query param: `?api_key=<key>`

## Next Actions

1. Determine which authentication method your API uses
2. Add proper auth headers to Postman collection
3. Update environment variables with correct credentials
4. Run tests again

## Files Created
- `newman-reports/auth-analysis.json` - Detailed analysis
- `newman-reports/fix-instructions.md` - This file