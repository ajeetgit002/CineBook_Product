@echo off
REM CineBook API Testing Script for Windows
REM This script helps test the APIs and verify logging works

setlocal enabledelayedexpansion
set BASE_URL=http://localhost:9099
set TOKEN=

echo.
echo ========================================
echo   CineBook API Testing Script (Windows)
echo ========================================
echo.

REM Test 1: Login
echo [1] Testing Login...
echo ========================================
curl -X POST %BASE_URL%/api/auth/login ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"admin@example.com\",\"password\":\"admin123\"}"
echo.
echo Note: Copy the token from response above
echo.

REM Test 2: Get Public Data
echo [2] Testing Public Endpoint (GET /api/movies)...
echo ========================================
curl -X GET %BASE_URL%/api/movies
echo.
echo Status: Should be 200 (public endpoint)
echo.

REM Test 3: Test without token
echo [3] Testing Protected Endpoint WITHOUT Token (GET /api/bookings/my)...
echo ========================================
curl -X GET %BASE_URL%/api/bookings/my -v
echo.
echo Expected Status: 403 Forbidden
echo Check logs for: [JWT Filter] No Bearer token found
echo.

REM Test 4: Test with token
echo [4] Ready to test WITH Token?
echo ========================================
echo Step 1: Copy the token from test [1] response
echo Step 2: Run this command (replace TOKEN with actual token):
echo.
echo curl -X GET %BASE_URL%/api/bookings/my ^
echo   -H "Authorization: Bearer TOKEN"
echo.

REM Instructions for checking logs
echo.
echo ========================================
echo   Checking Logs
echo ========================================
echo.
echo Option 1: View log file directly
echo   File: logs\cinebook.log
echo.
echo Option 2: Search for 403 errors
echo   findstr "403" logs\cinebook.log
echo.
echo Option 3: Real-time log monitoring (requires PowerShell)
echo   powershell -Command "Get-Content logs\cinebook.log -Wait"
echo.
echo Option 4: Search for JWT issues
echo   findstr "[JWT]" logs\cinebook.log
echo.

REM Documentation
echo ========================================
echo   For More Help
echo ========================================
echo.
echo 1. Read: QUICK_DEBUG_GUIDE.md
echo 2. Read: LOGGING_AND_DEBUGGING_GUIDE.md
echo 3. Read: LOGGING_IMPLEMENTATION_SUMMARY.md
echo.

pause

