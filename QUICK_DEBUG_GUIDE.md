# 🔧 QUICK DEBUG GUIDE - 403 Error

## ⚡ Quick Steps to Debug 403 Error

### Step 1: Rebuild & Restart
```bash
# Clear and rebuild
mvn clean package

# Start application
java -jar target/CineBook-1.0-SNAPSHOT.jar
```

### Step 2: Watch Logs
```bash
# In a new terminal/tab, watch logs in real-time
tail -f logs/cinebook.log
```

### Step 3: Reproduce Error
Make the same request that gives 403

### Step 4: Check Log Output
Look for these patterns in logs:

---

## 🎯 Common 403 Issues - A Quick Reference

### ❌ **No Bearer Token**
```
[JWT Filter] No Bearer token found for path: /api/bookings
```
**Fix**: Add header `Authorization: Bearer <your-token>`

### ❌ **Expired Token**
```
[JwtService] Token expired
```
**Fix**: Login again to get new token

### ❌ **Invalid Token**
```
[JwtService] Invalid token signature
```
**Fix**: Use token from login endpoint

### ❌ **User Not Found**
```
[UserDetailsService] User not found with email: user@example.com
```
**Fix**: Register user first

### ❌ **Wrong Role**
```
[UserDetailsService] User authorities: [ROLE_USER]
[SecurityConfig] Access denied for /api/admin/users: requires ROLE_ADMIN
```
**Fix**: Use correct user or endpoint

### ❌ **User Disabled**
```
[UserDetailsService] User found: user@example.com, Role: ROLE_USER, Enabled: false
```
**Fix**: Contact admin to enable account

---

## 📝 Example Log Trace - Fixed Request

### Before (403 Error):
```
[REQUEST] GET /api/bookings/my from 127.0.0.1
[JWT Filter] No Bearer token found for path: /api/bookings/my
[Exception] 403 Forbidden - Path: /api/bookings/my
[RESPONSE] GET /api/bookings/my - Status: 403 (5ms)
```

### After (200 Success):
```
[REQUEST] GET /api/bookings/my from 127.0.0.1
[REQUEST] Authorization: Bearer [TOKEN_PRESENT]
[JWT Filter] Bearer token found, extracting username...
[JWT Filter] Extracted email: admin@example.com
[JWT Filter] Successfully authenticated user: admin@example.com
[UserDetailsService] UserDetails created with authorities: [ROLE_ADMIN]
[RESPONSE] GET /api/bookings/my - Status: 200 (120ms)
```

---

## 🔍 Log Filtering Commands

```bash
# See all 403 errors
grep "403\|Forbidden" logs/cinebook.log

# See JWT issues
grep "\[JWT\]" logs/cinebook.log

# See authentication failures
grep "Authentication\|Unauthorized" logs/cinebook.log

# See specific request
grep "/api/bookings" logs/cinebook.log

# Real-time watch for specific pattern
tail -f logs/cinebook.log | grep "403"
```

---

## ✅ Test Sequence

### 1. Test Login
```bash
curl -X POST http://localhost:9099/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'

# You should get back a token
# Response: {"success":true, "data":{"token":"eyJ0eXAi..."}}
```

### 2. Copy the Token
```bash
TOKEN=eyJ0eXAi...  # Your token from login response
```

### 3. Test Booking with Token
```bash
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer $TOKEN"

# Should get 200 with booking data
```

### 4. Check Logs
```bash
tail -f logs/cinebook.log | tail -20
# Should see SUCCESS logs
```

---

## 🚨 If Still Getting 403:

1. **Check logs for REQUEST lines**
   - Verify Authorization header is present
   - Verify it says `Bearer [TOKEN_PRESENT]`

2. **Check JWT lines**
   - Look for token extraction success
   - Look for email extracted
   - Look for authorities loaded

3. **Check Exception lines**
   - See exact error message
   - Check which endpoint is failing
   - Verify user exists

4. **Check Response lines**
   - See final status code
   - See processing time
   - Confirm 403 status

---

## 📍 Log File Location
```
C:\Users\Ajit\CineBook\CineBook\logs\cinebook.log
```

---

## 🎬 End-to-End Test

```bash
# 1. Start watching logs
# Terminal 1:
tail -f logs/cinebook.log

# 2. In another terminal, get token
# Terminal 2:
TOKEN=$(curl -s -X POST http://localhost:9099/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"

# 3. Use token in request
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer $TOKEN"

# 4. Watch Terminal 1 for success logs!
```

---

## 💡 Pro Tips

1. **Filter for your endpoint only**
   ```bash
   grep "/api/bookings" logs/cinebook.log | tail -50
   ```

2. **Get just the errors**
   ```bash
   grep "Exception\|ERROR\|403\|401" logs/cinebook.log
   ```

3. **See request headers**
   ```bash
   grep "\[REQUEST\]" logs/cinebook.log | head -20
   ```

4. **Monitor in real-time with color**
   ```bash
   tail -f logs/cinebook.log | grep --color "403\|200\|401\|SUCCESS\|ERROR"
   ```

---

## 🔐 Security Note
Tokens are masked in logs as `Bearer [TOKEN_PRESENT]` for security.  
Never share logs with tokens visible.

---

**Still need help?** Check LOGGING_AND_DEBUGGING_GUIDE.md for detailed troubleshooting

