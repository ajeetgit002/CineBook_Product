# CineBook Logging & Debugging Guide

## Overview
Complete logging has been added to the CineBook application to debug the **403 Forbidden** error and other security issues.

---

## What Was Added

### 1. **Security Filter Logging** 
- **File**: `JwtAuthenticationFilter.java`
- **Logs**:
  - Request path and method for each request
  - Bearer token extraction
  - Username extraction from token
  - User authorities/roles
  - Authentication success/failure

### 2. **User Details Service Logging**
- **File**: `CustomUserDetailsService.java`
- **Logs**:
  - User lookup by email
  - User not found errors (with email details)
  - User role and enabled status
  - User authorities created

### 3. **Security Configuration Logging**
- **File**: `SecurityConfig.java`
- **Logs**:
  - Security filter chain initialization
  - CSRF/Session policy configuration
  - Authentication/Access denial reasons with request paths

### 4. **JWT Service Logging**
- **File**: `JwtService.java`
- **Logs**:
  - Token generation success
  - Token extraction with username
  - Token expiration errors
  - Invalid signature errors
  - Token processing errors

### 5. **Request Logging Filter** (NEW)
- **File**: `RequestLoggingFilter.java`
- **Logs**:
  - All incoming requests (method, path, query params)
  - Request headers (Authorization header masked for security)
  - Remote address
  - Response status
  - Request processing time
  - Warnings for 4xx/5xx errors

### 6. **Global Exception Handler**
- **File**: `GlobalExceptionHandler.java`
- **Logs**:
  - 401 Unauthorized errors
  - 403 Forbidden errors
  - 404 Not Found errors
  - 400 Bad Request/Validation errors
  - 500 Internal Server errors
  - Request paths for all errors

### 7. **Application Configuration**
- **File**: `application.yml`
- **Logging Configuration**:
  - Root logging level: INFO
  - CineBook application: DEBUG
  - Spring Security: DEBUG
  - Spring Security Web: DEBUG
  - Database: WARN (SQL queries at DEBUG level)
  - Log file: `logs/cinebook.log`
  - Max file size: 10MB, 30 day history

---

## How to Debug the 403 Error

### Step 1: Check the Logs

When you get a **403 Forbidden** error, immediately check the logs:

```bash
# For development (console output)
# Watch the console in your IDE or terminal

# For file logs
tail -f logs/cinebook.log | grep -E "\[REQUEST\]|\[Exception\]|\[JWT\]|\[Security"
```

### Step 2: Look for These Log Patterns

#### **Case: Missing Bearer Token**
```
[JWT Filter] No Bearer token found for path: /api/bookings/my
[RESPONSE] GET /api/bookings/my - Status: 403 (5ms)
```
**Solution**: Include Authorization header: `Authorization: Bearer <your-token>`

#### **Case: Invalid/Expired Token**
```
[JWT Filter] Bearer token found, extracting username...
[JwtService] Token expired
[JWT Filter] Authentication error for path: /api/bookings/my - Token has expired
[RESPONSE] GET /api/bookings/my - Status: 403 (10ms)
```
**Solution**: Generate a new token using LOGIN endpoint

#### **Case: User Not Found**
```
[JwtService] Token extracted username: user@example.com
[UserDetailsService] Loading user details for email: user@example.com
[UserDetailsService] User not found with email: user@example.com
[Exception] 401 Unauthorized - Path: /api/bookings/my, Message: User not found: user@example.com
```
**Solution**: Register the user first

#### **Case: Insufficient Permissions**
```
[JWT Filter] Successfully authenticated user: admin@example.com for path: /api/admin/users
[UserDetailsService] User authorities: [ROLE_ADMIN]
[Exception] 403 Forbidden - Path: /api/admin/users, Message: User does not have required role
```
**Solution**: Use correct user role or endpoint

#### **Case: User Disabled**
```
[UserDetailsService] User found: user@example.com, Role: ROLE_USER, Enabled: false
[UserDetailsService] UserDetails created with authorities: [ROLE_USER]
[Exception] 401 Unauthorized - Path: /api/bookings/my
```
**Solution**: Enable the user account

### Step 3: Verify Request Format

Check the REQUEST logs to ensure your request is correct:

```
[REQUEST] POST /api/bookings from 127.0.0.1 Headers: 
[REQUEST] Authorization: Bearer [TOKEN_PRESENT]
[REQUEST] Header - content-type: application/json
[REQUEST] Header - content-length: 245
[RESPONSE] POST /api/bookings - Status: 201 (150ms)
```

---

## Common 403 Issues & Solutions

| Issue | Log Pattern | Solution |
|-------|-------------|----------|
| No Bearer Token | "No Bearer token found" | Add `Authorization: Bearer <token>` header |
| Expired Token | "Token expired" | Login again to get new token |
| Invalid Token | "Invalid token signature" | Use token from login endpoint |
| User Not Found | "User not found with email" | Register user first |
| Wrong User Role | "User does not have required role" | Use correct user account or endpoint |
| User Disabled | "Enabled: false" | Contact admin to enable account |
| CSRF Issue | 403 response with empty body | Token validation failed - check token |

---

## Enabling More Detailed Logging

If you need even more debug information, update `application.yml`:

### For Very Detailed Logging:
```yaml
logging:
  level:
    com.cinebook: TRACE
    org.springframework.security: TRACE
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### For Production (Reduced Logging):
```yaml
logging:
  level:
    root: WARN
    com.cinebook: INFO
    org.springframework.security: WARN
```

---

## Log File Location

All logs are saved to:
```
logs/cinebook.log
```

Log rotation is configured:
- **Max file size**: 10MB
- **Max history**: 30 days
- **Total size cap**: 1GB

---

## Understanding Log Levels

| Level | Use Case | Example |
|-------|----------|---------|
| TRACE | Very detailed debugging | SQL parameter binding |
| DEBUG | Detailed debugging info | Token extraction, user lookup |
| INFO | Important info | Token generated, user authenticated |
| WARN | Warning situations | Token expired, 403 errors |
| ERROR | Error situations | User not found, signup failed |

---

## Test the Logging

### 1. Test Successful Login
```bash
curl -X POST http://localhost:9099/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","password":"password123"}'

# Check logs for: 
# [JwtService] Token generated successfully for email: user@example.com
# [RESPONSE] POST /api/auth/login - Status: 200
```

### 2. Test Successful Booking
```bash
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer <your-token-here>"

# Check logs for:
# [REQUEST] GET /api/bookings/my from 127.0.0.1
# [JWT Filter] Successfully authenticated user: user@example.com for path: /api/bookings/my
# [RESPONSE] GET /api/bookings/my - Status: 200
```

### 3. Test Missing Token (403 Error)
```bash
curl -X GET http://localhost:9099/api/bookings/my

# Check logs for:
# [REQUEST] GET /api/bookings/my from 127.0.0.1
# [JWT Filter] No Bearer token found for path: /api/bookings/my
# [Exception] 403 Forbidden - Path: /api/bookings/my
# [RESPONSE] GET /api/bookings/my - Status: 403
```

### 4. Test Invalid Token
```bash
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer invalid-token-here"

# Check logs for:
# [JWT Filter] Bearer token found, extracting username...
# [JwtService] Error extracting username from token
# [JWT Filter] Authentication error for path: /api/bookings/my
```

---

## Troubleshooting the Logs

### Logs not appearing?
1. **Verify application started**: Check for `[SecurityConfig] Security filter chain configured successfully`
2. **Check log file**: `logs/cinebook.log` exists and has content
3. **Check log level**: Ensure `com.cinebook: DEBUG` in `application.yml`
4. **Restart application**: Changes to `application.yml` require restart

### Logs are too verbose?
1. Reduce log levels in `application.yml`
2. Filter by component: `grep "\[JWT\]" logs/cinebook.log`
3. Use log analysis tools

### Need real-time logs?
```bash
# Tail logs in real-time
tail -f logs/cinebook.log

# Filter for errors only
tail -f logs/cinebook.log | grep ERROR

# Filter for 403 errors
tail -f logs/cinebook.log | grep "403\|Forbidden"
```

---

## Summary

✅ **Comprehensive logging** is now added to:
- Security filters
- JWT token processing
- User authentication
- Authorization checks
- All API responses
- Exception handling

✅ **Easy debugging** of 403 errors with:
- Request/response tracking
- Token validation details
- User role verification
- Error messages with context

✅ **Production-ready** with:
- Configurable log levels
- Log file rotation
- Key information masked (tokens)
- Performance metrics (response times)

---

**Start debugging your 403 error now!**

Next steps:
1. Rebuild your application
2. Start the application
3. Try your request again
4. Check `logs/cinebook.log` for detailed information
5. Use the troubleshooting guide above

---

Last Updated: June 1, 2026

