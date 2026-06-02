# ⚡ ACTION ITEMS - Get Logging Working (5 Minutes)

## ✅ What Was Done (Already Complete)
- [x] Added logging to JwtAuthenticationFilter.java
- [x] Added logging to CustomUserDetailsService.java
- [x] Added logging to SecurityConfig.java  
- [x] Added logging to JwtService.java
- [x] Added logging to GlobalExceptionHandler.java
- [x] Created RequestLoggingFilter.java (NEW)
- [x] Updated application.yml with logging config
- [x] Created 7 documentation files
- [x] Created test scripts (batch, bash, HTTP)
- [x] Code compiles successfully ✅

## 🎯 What You Need To Do Now (5 Steps)

### Step 1: Rebuild Application (2 minutes)
```bash
cd C:\Users\Ajit\CineBook\CineBook
mvn clean package
```

**Expected Output**:
```
BUILD SUCCESS
Total time: XX.XXXs
CineBook-1.0-SNAPSHOT.jar created
```

### Step 2: Start Application (1 minute)
```bash
java -jar target/CineBook-1.0-SNAPSHOT.jar
```

**Expected Output**:
```
Starting CineBookApplication...
Tomcat started on port 9099
Application ready
```

### Step 3: Watch Logs (Keep Running)
```bash
# PowerShell (Windows):
Get-Content logs/cinebook.log -Wait

# OR Command Prompt:
type logs\cinebook.log && timeout /t 1 /nobreak && cls && type logs\cinebook.log && goto :repeat
```

**Expected**: Logs will start appearing as requests come in

### Step 4: Test with 403 Error Request (1 minute)
In another terminal:
```bash
# This should fail with 403 (no auth token)
curl -X GET http://localhost:9099/api/bookings/my
```

**Check Terminal with logs**: 
You should see detailed logs explaining the 403 error!

### Step 5: Test with Valid Token (1 minute)
```bash
# 1. Login to get token
TOKEN=$(curl -s -X POST http://localhost:9099/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Your Token: $TOKEN"

# 2. Use the token
curl -X GET http://localhost:9099/api/bookings/my \
  -H "Authorization: Bearer $TOKEN"
```

**Check Terminal with logs**: 
You should see SUCCESS logs showing authentication worked!

---

## 📋 Files to Know About

### Documentation (Read in This Order)
1. **README_LOGGING_SETUP.md** ← START HERE
2. **QUICK_DEBUG_GUIDE.md** ← For quick fixes
3. **LOGGING_AND_DEBUGGING_GUIDE.md** ← For detailed help
4. **LOGGING_IMPLEMENTATION_SUMMARY.md** ← For technical details

### Test Files (Choose One)
1. **test_apis.http** ← Use in IntelliJ (EASIEST!)
2. **test_apis.bat** ← Use in Windows Command Prompt
3. **test_apis.sh** ← Use in Bash/WSL

### Code Files Modified
- `JwtAuthenticationFilter.java` - Added token logging
- `CustomUserDetailsService.java` - Added user lookup logging
- `SecurityConfig.java` - Added authorization logging
- `JwtService.java` - Added token operations logging
- `GlobalExceptionHandler.java` - Added error logging
- `RequestLoggingFilter.java` - NEW: Request/response logging
- `application.yml` - Added logging configuration

---

## 🎯 Expected Behavior After Setup

### When You Make a 403 Error Request:
**Before**: No logs, just empty 403 response
```
Error: response status is 403
```

**After**: Detailed logs showing exactly why!
```
[REQUEST] GET /api/bookings from 127.0.0.1
[JWT Filter] No Bearer token found for path: /api/bookings
[Exception] 403 Forbidden - Path: /api/bookings
[RESPONSE] GET /api/bookings - Status: 403 (5ms)
```

### When Permission is Denied:
```
[REQUEST] GET /api/admin/users from 127.0.0.1
[JWT Filter] Successfully authenticated user: user@example.com
[UserDetailsService] User authorities: [ROLE_USER]
[Exception] 403 Forbidden - User does not have required role ROLE_ADMIN
[RESPONSE] GET /api/admin/users - Status: 403 (8ms)
```

### When Everything Works:
```
[REQUEST] GET /api/bookings from 127.0.0.1
[JWT Filter] Successfully authenticated user: admin@example.com
[UserDetailsService] User authorities: [ROLE_ADMIN]
[RESPONSE] GET /api/bookings - Status: 200 (120ms)
```

---

## 🧪 Quick Test Methods

### Option 1: IntelliJ HTTP Client (RECOMMENDED - Easiest)
1. Open `test_apis.http` in IntelliJ
2. Press: `Ctrl+Alt+Shift+R`
3. Watch responses and logs

### Option 2: Windows Command Line
```bash
test_apis.bat
```

### Option 3: PowerShell One-Liner
```powershell
# Login
$token = (curl -s -X POST http://localhost:9099/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"email":"admin@example.com","password":"admin123"}' | 
  ConvertFrom-Json).data.token

# Use token
curl -X GET http://localhost:9099/api/bookings `
  -H "Authorization: Bearer $token"
```

### Option 4: Manual CURL
```bash
# Get token
curl -X POST http://localhost:9099/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'

# Copy token and use it
curl -X GET http://localhost:9099/api/bookings \
  -H "Authorization: Bearer <PASTE_TOKEN_HERE>"
```

---

## 🔍 Troubleshooting Quick Fixes

| Issue | Quick Fix |
|-------|-----------|
| App won't start | Check port 9099 is free: `netstat -ano \| findstr :9099` |
| No logs appear | Check `logs/` directory exists and has write permissions |
| Build fails | Run: `mvn clean install` |
| Token is null | Verify user exists: `admin@example.com` / `admin123` |
| Still 403 error | Check logs! Follow QUICK_DEBUG_GUIDE.md |

---

## 📊 Verification Checklist

Use this to verify everything is working:

- [ ] Maven build successful? ✅
- [ ] Application started on port 9099? ✅
- [ ] logs/cinebook.log file created? ✅
- [ ] Can see logs in real-time? ✅
- [ ] 403 error shows logs? ✅
- [ ] Successful request shows 200 logs? ✅
- [ ] Can identify token issues from logs? ✅
- [ ] Can identify permission issues from logs? ✅

---

## 💬 Example Conversation with Logs

### You: "Why am I getting 403?"
**Logs show**:
```
[JWT Filter] No Bearer token found for path: /api/bookings
```
**You now know**: Need to add Authorization header

### You: "Why doesn't my token work?"
**Logs show**:
```
[JwtService] Token expired
```
**You now know**: Need to login again to get new token

### You: "Why can't my user admin something?"
**Logs show**:
```
[UserDetailsService] User authorities: [ROLE_USER]
[Exception] 403 Forbidden - User does not have required role ROLE_ADMIN
```
**You now know**: User needs ROLE_ADMIN role

---

## 🚀 Ready? Let's Go!

### Command to Copy & Paste:
```bash
# 1. Rebuild
cd C:\Users\Ajit\CineBook\CineBook
mvn clean package

# 2. Watch logs (in background or new terminal)
Get-Content logs/cinebook.log -Wait

# 3. Start app (replace PORT if needed)
java -jar target/CineBook-1.0-SNAPSHOT.jar

# 4. In another terminal, test
curl -X GET http://localhost:9099/api/bookings/my
```

---

## 📚 Documentation URLs

All files are in: `C:\Users\Ajit\CineBook\CineBook\`

- `README_LOGGING_SETUP.md` - Main guide
- `QUICK_DEBUG_GUIDE.md` - Quick help
- `LOGGING_AND_DEBUGGING_GUIDE.md` - Detailed help
- `COMPLETION_SUMMARY.md` - What was done

---

## ✅ Success Criteria

✅ You can run the app  
✅ You see logs appear in real-time  
✅ 403 errors show detailed logs  
✅ Successful requests show 200 status  
✅ You can debug issues by reading logs  

**If all above are checked, you're DONE!** 🎉

---

## 🎓 Next Learning

Once basic setup works:
1. Try different endpoints
2. Read LOGGING_AND_DEBUGGING_GUIDE.md
3. Understand each log pattern
4. Set up custom alerts (optional)
5. Integrate with monitoring tools (optional)

---

**Time to Complete**: ~10 minutes  
**Level**: Beginner friendly  
**Status**: ✅ READY TO GO

Need help? Check the documentation files! They have everything explained.

Good luck! 🚀

