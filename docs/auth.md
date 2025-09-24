# ELRR Services Authentication

## Token Endpoint

To get a token and make API calls you can POST a request to the `/admin/token` endpoint. You will need a JWT with the proper role in the payload to satisfy the application's `admin.jwt.role` and `admin.jwt.role-key` settings, for instance:

```json
{
  "iss": "http://example.com",
  "iat": 1750169624,
  "preferred_username": "admin-user",
  "group-simple": [
    "elrr-admin"
  ]
}
```

Encoded as a JWT: `eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vZXhhbXBsZS5jb20iLCJpYXQiOjE3NTAxNjk2MjQsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVzZXIiLCJncm91cC1zaW1wbGUiOlsiZWxyci1hZG1pbiJdfQ.d3NhUMEoDfKqB6Iii_SA1AiAkqPQ01q74IJ_BC44iEoNQbCih_tBICKVLzxC7bvao1riorB2ygv7-rOP0WpM0w`

Call the API using the JWT. In the body of the POST request, specify the desired token permissions (note that the displayed permissions allow any action and are only suitable for development):

```http
POST /admin/token HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vZXhhbXBsZS5jb20iLCJpYXQiOjE3NTAxNjk2MjQsInByZWZlcnJlZF91c2VybmFtZSI6ImFkbWluLXVzZXIiLCJncm91cC1zaW1wbGUiOlsiZWxyci1hZG1pbiJdfQ.d3NhUMEoDfKqB6Iii_SA1AiAkqPQ01q74IJ_BC44iEoNQbCih_tBICKVLzxC7bvao1riorB2ygv7-rOP0WpM0w
Content-Type: application/json
Host: localhost:8092
Connection: close
User-Agent: RapidAPI/4.2.0 (Macintosh; OS X/15.5.0) GCDHTTPRequest
Content-Length: 252

{
  "label": "milt-test",
  "permissions": [
    {
      "resource": "*",
      "resourceId": null,
      "actions": [
        "CREATE",
        "READ",
        "UPDATE",
        "DELETE",
        "ASSOCIATE",
        "DISASSOCIATE"
      ]
    }
  ]
}
```

The JSON response will contain a token you can use to access the rest of the API in the "token" field:

```http
HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Transfer-Encoding: chunked
Date: Mon, 14 Jul 2025 15:47:30 GMT
Connection: close

{"id":"0198099e-aa7a-7fd1-95f6-d29780738e5c","insertedDate":null,"updatedBy":null,"lastModified":null,"token":"eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vZWxyci5leGFtcGxlLmNvbSIsImlhdCI6MTc1MjUwODA1MCwianRpIjoiMDE5ODA5OWUtYWE2Yy03MjBlLWE0MDMtNTVmMjQ1MGUyOWRhIiwidG9rZW4tY3JlYXRvciI6ImFkbWluLXVzZXIiLCJlbHJyX3Blcm1pc3Npb25zIjpbeyJyZXNvdXJjZUlkIjpudWxsLCJyZXNvdXJjZSI6IioiLCJhY3Rpb25zIjpbIkNSRUFURSIsIlJFQUQiLCJVUERBVEUiLCJERUxFVEUiLCJBU1NPQ0lBVEUiLCJESVNBU1NPQ0lBVEUiXX1dfQ.RxV1m4C2dwtkA3hzw3i_1jvy1TGigEYaM3xWDaKXsznmbR6yh-YWpQ53pIC0pAYyZqgKtNdV19jyir3hO9n6Eg","jwtId":"0198099e-aa6c-720e-a403-55f2450e29da","label":"milt-test"}
```

The token `eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vZWxyci5leGFtcGxlLmNvbSIsImlhdCI6MTc1MjUwODA1MCwianRpIjoiMDE5ODA5OWUtYWE2Yy03MjBlLWE0MDMtNTVmMjQ1MGUyOWRhIiwidG9rZW4tY3JlYXRvciI6ImFkbWluLXVzZXIiLCJlbHJyX3Blcm1pc3Npb25zIjpbeyJyZXNvdXJjZUlkIjpudWxsLCJyZXNvdXJjZSI6IioiLCJhY3Rpb25zIjpbIkNSRUFURSIsIlJFQUQiLCJVUERBVEUiLCJERUxFVEUiLCJBU1NPQ0lBVEUiLCJESVNBU1NPQ0lBVEUiXX1dfQ.RxV1m4C2dwtkA3hzw3i_1jvy1TGigEYaM3xWDaKXsznmbR6yh-YWpQ53pIC0pAYyZqgKtNdV19jyir3hO9n6Eg` contains the permissions in a claim:

```json
{
  "iss": "http://elrr.example.com",
  "iat": 1752508050,
  "jti": "0198099e-aa6c-720e-a403-55f2450e29da",
  "token-creator": "admin-user",
  "elrr_permissions": [
    {
      "resourceId": null,
      "resource": "*",
      "actions": [
        "CREATE",
        "READ",
        "UPDATE",
        "DELETE",
        "ASSOCIATE",
        "DISASSOCIATE"
      ]
    }
  ]
}
```

## Authorized API Call

Using the token from the previous section we can call the API:

```http
GET /api/person HTTP/1.1
Authorization: Bearer eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwOi8vZWxyci5leGFtcGxlLmNvbSIsImlhdCI6MTc1MjUwODA1MCwianRpIjoiMDE5ODA5OWUtYWE2Yy03MjBlLWE0MDMtNTVmMjQ1MGUyOWRhIiwidG9rZW4tY3JlYXRvciI6ImFkbWluLXVzZXIiLCJlbHJyX3Blcm1pc3Npb25zIjpbeyJyZXNvdXJjZUlkIjpudWxsLCJyZXNvdXJjZSI6IioiLCJhY3Rpb25zIjpbIkNSRUFURSIsIlJFQUQiLCJVUERBVEUiLCJERUxFVEUiLCJBU1NPQ0lBVEUiLCJESVNBU1NPQ0lBVEUiXX1dfQ.RxV1m4C2dwtkA3hzw3i_1jvy1TGigEYaM3xWDaKXsznmbR6yh-YWpQ53pIC0pAYyZqgKtNdV19jyir3hO9n6Eg
Content-Type: application/json
Host: localhost:8092
Connection: close
User-Agent: RapidAPI/4.2.0 (Macintosh; OS X/15.5.0) GCDHTTPRequest
```

And get back data:

```http
HTTP/1.1 200 
Vary: Origin
Vary: Access-Control-Request-Method
Vary: Access-Control-Request-Headers
X-Content-Type-Options: nosniff
X-XSS-Protection: 0
Cache-Control: no-cache, no-store, max-age=0, must-revalidate
Pragma: no-cache
Expires: 0
X-Frame-Options: DENY
Content-Type: application/json
Transfer-Encoding: chunked
Date: Fri, 11 Jul 2025 15:46:52 GMT
Connection: close

[{"id":"0197fa29-957f-7d48-825a-e0f0973000b4","insertedDate":"2025-07-11T11:45:18.721718","updatedBy":null
...
```

## Admin API Override Mode

In this mode (enabled by setting property `client.admin-api-override` or env `ELRR_ADMIN_API_OVERRIDE` to true), the Admin JWT which normally can only request an API token is able to perform all API calls as a bearer token.

**DANGER**: This is not recommended for production deployments and proper API tokens should be created instead.
