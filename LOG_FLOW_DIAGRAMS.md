# 📊 Log Flow Diagrams - Visual Guide

## 🎯 Request Flow with Logging

### Scenario 1: Request WITHOUT Token (403 Error)

```
USER REQUEST
    ↓
curl -X GET http://localhost:9099/api/bookings/my
    ↓
[REQUEST] GET /api/bookings/my from 127.0.0.1
[REQUEST] Authorization: NONE
    ↓
RequestLoggingFilter (NEW)
    ↓
JwtAuthenticationFilter
    ↓
Check Header: "Authorization: Bearer ..."?
    ↓
[JWT Filter] No Bearer token found for path: /api/bookings/my
[DEBUG LOG]
    ↓
SecurityConfig Authorization Check
    ↓
[SecurityConfig] Access denied for /api/bookings/my
    ↓
GlobalExceptionHandler
    ↓
[Exception] 403 Forbidden - Path: /api/bookings/my
[WARN LOG]
    ↓
Response Status 403
    ↓
[RESPONSE] GET /api/bookings/my - Status: 403 (5ms)
[INFO LOG]
    ↓
USER RECEIVES: 403 FORBIDDEN
WITH DETAILED LOGS SHOWING EXACTLY WHY!
```

---

### Scenario 2: Request WITH Valid Token (200 Success)

```
USER REQUEST
    ↓
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer eyJ0eXAi..."
    ↓
[REQUEST] GET /api/bookings/my from 127.0.0.1
[REQUEST] Authorization: Bearer [TOKEN_PRESENT]
[INFO LOG]
    ↓
RequestLoggingFilter (NEW)
    ↓
JwtAuthenticationFilter
    ↓
Check Header: "Authorization: Bearer ..."?
    ↓
[JWT Filter] Bearer token found, extracting username...
[DEBUG LOG]
    ↓
JwtService.extractUsername(token)
    ↓
[JwtService] Token extracted username: admin@example.com
[DEBUG LOG]
    ↓
CustomUserDetailsService.loadUserByUsername(email)
    ↓
[UserDetailsService] Loading user details for email: admin@example.com
[UserDetailsService] User found: admin@example.com, Role: ROLE_ADMIN, Enabled: true
[UserDetailsService] UserDetails created with authorities: [ROLE_ADMIN]
[DEBUG LOGS]
    ↓
SecurityContextHolder.setAuthentication(auth)
    ↓
[JWT Filter] Successfully authenticated user: admin@example.com for path: /api/bookings/my
[INFO LOG]
    ↓
SecurityConfig Authorization Check
    ↓
Does user have required role?
USER has: [ROLE_ADMIN]
ENDPOINT requires: [ROLE_USER, ROLE_ADMIN]
    ↓
✓ AUTHORIZED
    ↓
Execute BookingController.myBookings()
    ↓
Response Status 200
    ↓
[RESPONSE] GET /api/bookings/my - Status: 200 (120ms)
[INFO LOG]
    ↓
USER RECEIVES: 200 OK WITH DATA
WITH LOGS SHOWING SUCCESSFUL AUTHENTICATION!
```

---

### Scenario 3: Request WITH Expired Token (401 Error)

```
USER REQUEST
    ↓
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer eyJ0eXAi..." (OLD TOKEN)
    ↓
[REQUEST] GET /api/bookings/my from 127.0.0.1
[REQUEST] Authorization: Bearer [TOKEN_PRESENT]
[INFO LOG]
    ↓
JwtAuthenticationFilter
    ↓
[JWT Filter] Bearer token found, extracting username...
[DEBUG LOG]
    ↓
JwtService.extractUsername(token)
    ↓
try {
    PARSE TOKEN
    CHECK SIGNATURE
    CHECK EXPIRATION ← TOKEN IS EXPIRED!
}
    ↓
[JwtService] Token expired
[WARN LOG]
    ↓
throw RuntimeException("Token has expired")
    ↓
[JWT Filter] Authentication error for path: /api/bookings/my - Token has expired
[ERROR LOG]
    ↓
GlobalExceptionHandler
    ↓
[Exception] 401 Unauthorized - Path: /api/bookings/my, Message: Token has expired
[WARN LOG]
    ↓
Response Status 401
    ↓
[RESPONSE] GET /api/bookings/my - Status: 401 (10ms)
[WARN LOG]
    ↓
USER RECEIVES: 401 UNAUTHORIZED
WITH LOGS CLEARLY SHOWING TOKEN IS EXPIRED!
```

---

### Scenario 4: Request WITH Invalid Token (403 Error)

```
USER REQUEST
    ↓
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer invalid.token.here"
    ↓
[REQUEST] GET /api/bookings/my from 127.0.0.1
[REQUEST] Authorization: Bearer [TOKEN_PRESENT]
[INFO LOG]
    ↓
JwtAuthenticationFilter
    ↓
[JWT Filter] Bearer token found, extracting username...
[DEBUG LOG]
    ↓
JwtService.extractUsername(token)
    ↓
try {
    PARSE TOKEN ← INVALID FORMAT!
    CHECK SIGNATURE ← SIGNATURE VERIFICATION FAILED!
}
    ↓
[JwtService] Invalid token signature
[WARN LOG]
    ↓
throw RuntimeException("Invalid token signature")
    ↓
[JWT Filter] Authentication error for path: /api/bookings/my - Invalid token signature
[ERROR LOG]
    ↓
GlobalExceptionHandler
    ↓
[Exception] 403 Forbidden - Path: /api/bookings/my
[WARN LOG]
    ↓
Response Status 403
    ↓
[RESPONSE] GET /api/bookings/my - Status: 403 (8ms)
[WARN LOG]
    ↓
USER RECEIVES: 403 FORBIDDEN
WITH LOGS CLEARLY SHOWING INVALID SIGNATURE!
```

---

### Scenario 5: Request WITH Valid Token BUT User Not Found (401 Error)

```
USER REQUEST
    ↓
(Assume token is valid but references deleted user)
    ↓
[REQUEST] GET /api/bookings/my from 127.0.0.1
[REQUEST] Authorization: Bearer [TOKEN_PRESENT]
[INFO LOG]
    ↓
JwtAuthenticationFilter
    ↓
[JWT Filter] Bearer token found, extracting username...
[AUTH Filter] Extracted email: deleted@example.com
[DEBUG LOG]
    ↓
CustomUserDetailsService.loadUserByUsername("deleted@example.com")
    ↓
Query Database: SELECT * FROM users WHERE email = ?
RESULT: NOT FOUND!
    ↓
[UserDetailsService] User not found with email: deleted@example.com
[ERROR LOG]
    ↓
throw UsernameNotFoundException("User not found: deleted@example.com")
    ↓
[JWT Filter] Authentication error for path: /api/bookings/my - User not found
[ERROR LOG]
    ↓
GlobalExceptionHandler.handleUnauthorized()
    ↓
[Exception] 401 Unauthorized - Path: /api/bookings/my, Message: User not found: deleted@example.com
[WARN LOG]
    ↓
Response Status 401
    ↓
[RESPONSE] GET /api/bookings/my - Status: 401 (12ms)
[WARN LOG]
    ↓
USER RECEIVES: 401 UNAUTHORIZED
WITH LOGS CLEARLY SHOWING USER WAS DELETED!
```

---

### Scenario 6: Request WITH Valid Token BUT User Disabled (401 Error)

```
USER REQUEST
    ↓
[REQUEST] GET /api/bookings/my from 127.0.0.1
[REQUEST] Authorization: Bearer [TOKEN_PRESENT]
[INFO LOG]
    ↓
JwtAuthenticationFilter
    ↓
[JWT Filter] Bearer token found, extracting username...
[JWT Filter] Extracted email: user@example.com
[DEBUG LOG]
    ↓
CustomUserDetailsService.loadUserByUsername("user@example.com")
    ↓
Query Database: SELECT * FROM users WHERE email = ?
RESULT: FOUND user, but enabled = false
    ↓
[UserDetailsService] User found: user@example.com, Role: ROLE_USER, Enabled: false
[WARN LOG]
    ↓
Create UserDetails with:
    username: user@example.com
    password: ***masked***
    authorities: [ROLE_USER]
    disabled: TRUE ← KEY INDICATOR!
    ↓
[UserDetailsService] UserDetails created with authorities: [ROLE_USER]
[DEBUG LOG]
    ↓
Add disabled UserDetails to SecurityContext
    ↓
SecurityConfig Authorization Check
    ↓
IsUserEnabled? NO!
    ↓
Access Denied
    ↓
[Exception] 401/403 Unauthorized/Forbidden
[WARN LOG]
    ↓
Response Status 401/403
    ↓
[RESPONSE] GET /api/bookings/my - Status: 401/403 (15ms)
    ↓
USER RECEIVES: 401/403 ERROR
WITH LOGS CLEARLY SHOWING USER IS DISABLED!
```

---

## 📋 Log Entry Reference

### Request Log Entry
```
[REQUEST] METHOD /path/to/endpoint from IP_ADDRESS Headers:
[REQUEST] Authorization: Bearer [TOKEN_PRESENT] | NONE
[REQUEST] Header - header-name: header-value
```

### JWT Filter Log Entry
```
[JWT Filter] Processing METHOD request to: /path/to/endpoint
[JWT Filter] Bearer token found, extracting username...
[JWT Filter] Extracted email: user@example.com
[JWT Filter] Successfully authenticated user: user@example.com for path: /path
[JWT Filter] Authentication error for path: /path - error-message
```

### User Details Service Log Entry
```
[UserDetailsService] Loading user details for email: user@example.com
[UserDetailsService] User not found with email: user@example.com
[UserDetailsService] User found: email, Role: ROLE_NAME, Enabled: true/false
[UserDetailsService] UserDetails created with authorities: [ROLE_NAME]
```

### JWT Service Log Entry
```
[JwtService] Token generated successfully for email: user@example.com
[JwtService] Token extracted username: user@example.com
[JwtService] Token expired
[JwtService] Invalid token signature
[JwtService] Error extracting username from token - reason
```

### Exception Log Entry
```
[Exception] 401 Unauthorized - Path: /path/to/endpoint, Message: reason
[Exception] 403 Forbidden - Path: /path/to/endpoint, Message: reason
[Exception] 404 Not Found - Path: /path/to/endpoint, Message: reason
[Exception] 400 Bad Request - Path: /path/to/endpoint, Message: reason
[Exception] 500 Internal Server Error - Path: /path/to/endpoint, Error: reason
```

### Response Log Entry
```
[RESPONSE] METHOD /path/to/endpoint - Status: HTTP_CODE (XXXms)
```

---

## 🎯 Quick Diagnosis Guide

### See `[JWT Filter] No Bearer token found`?
**Problem**: Missing Authorization header  
**Solution**: Add `Authorization: Bearer <token>`

### See `[JwtService] Token expired`?
**Problem**: Token is old/expired  
**Solution**: Login again with `/api/auth/login`

### See `[JwtService] Invalid token signature`?
**Problem**: Token is corrupted/modified  
**Solution**: Get new token from login endpoint

### See `[UserDetailsService] User not found`?
**Problem**: User was deleted or doesn't exist  
**Solution**: Register user or verify email

### See `User authorities: [ROLE_USER]` but need ADMIN?
**Problem**: User has wrong role  
**Solution**: Assign ROLE_ADMIN to user

### See `Enabled: false`?
**Problem**: User account is disabled  
**Solution**: Admin should enable user

---

## 📊 Common Error Codes & Meanings

| Code | Name | Meaning | Fix |
|------|------|---------|-----|
| 200 | OK | Success! | None needed |
| 401 | Unauthorized | User identity issue | Check user/token |
| 403 | Forbidden | Permission issue | Check role/permissions |
| 404 | Not Found | Resource missing | Check endpoint/path |
| 400 | Bad Request | Invalid data | Check request format |
| 500 | Server Error | Application error | Check server logs |

---

## 🔍 Real-Time Monitoring

### Watch Specific Errors:
```bash
# Only 403 errors
tail -f logs/cinebook.log | grep "403\|Forbidden"

# Only JWT issues
tail -f logs/cinebook.log | grep "\[JWT\]"

# Only user lookup issues
tail -f logs/cinebook.log | grep "\[UserDetailsService\]"

# Only authentication failures
tail -f logs/cinebook.log | grep "Authentication\|Unauthorized"

# Only exceptions
tail -f logs/cinebook.log | grep "\[Exception\]"
```

---

## 💡 Performance Insights

Response time in logs shows processing speed:

```
[RESPONSE] GET /api/bookings - Status: 200 (120ms)
                                             ^^^^^^
                                          This is fast!

[RESPONSE] GET /api/bookings - Status: 200 (2500ms)
                                             ^^^^^^^
                                          This is slow - investigate!
```

---

**Use these diagrams to understand your logs better!**

Each scenario shows exactly which logs appear and what they mean. 🎯

