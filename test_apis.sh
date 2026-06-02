#!/bin/bash
# CineBook API Testing Script
# This script helps test the APIs and verify logging works

BASE_URL="http://localhost:9099"
TOKEN=""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  CineBook API Testing Script${NC}"
echo -e "${BLUE}========================================${NC}"

# Function to print request
print_request() {
    echo ""
    echo -e "${YELLOW}[REQUEST]${NC} $1"
}

# Function to print response
print_response() {
    echo -e "${GREEN}[RESPONSE]${NC} Status: $1"
}

# Function to extract token from response
extract_token() {
    echo "$1" | grep -o '"token":"[^"]*' | head -1 | cut -d'"' -f4
}

# Test 1: Login (Get Token)
print_request "POST /api/auth/login"
RESPONSE=$(curl -s -X POST "$BASE_URL/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}')

STATUS=$(echo "$RESPONSE" | grep -o '"success":true\|"success":false' | head -1)
if [[ $STATUS == *"true"* ]]; then
    TOKEN=$(extract_token "$RESPONSE")
    if [ -z "$TOKEN" ]; then
        echo -e "${RED}Failed to extract token${NC}"
        echo "Response: $RESPONSE"
    else
        echo -e "${GREEN}✓ Login successful${NC}"
        echo "Token: ${TOKEN:0:20}..."
    fi
else
    echo -e "${RED}✗ Login failed${NC}"
    echo "Response: $RESPONSE"
    exit 1
fi

# Test 2: Get My Bookings (with token)
print_request "GET /api/bookings/my (with token)"
RESPONSE=$(curl -s -X GET "$BASE_URL/api/bookings/my" \
  -H "Authorization: Bearer $TOKEN")

if echo "$RESPONSE" | grep -q "success"; then
    echo -e "${GREEN}✓ Bookings retrieved${NC}"
else
    echo -e "${RED}✗ Failed to get bookings${NC}"
fi

# Test 3: Try without token (should fail with 403)
print_request "GET /api/bookings/my (without token - should fail)"
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/bookings/my")
if [ "$HTTP_STATUS" == "403" ]; then
    echo -e "${GREEN}✓ Got expected 403 error${NC}"
    echo -e "Check logs for: ${YELLOW}[JWT Filter] No Bearer token found${NC}"
else
    echo -e "${YELLOW}⚠ Expected 403 but got $HTTP_STATUS${NC}"
fi

# Test 4: Get movies (public endpoint, should work without token)
print_request "GET /api/movies (public endpoint)"
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/movies")
if [ "$HTTP_STATUS" == "200" ]; then
    echo -e "${GREEN}✓ Public endpoint works${NC}"
else
    echo -e "${RED}✗ Public endpoint failed with status $HTTP_STATUS${NC}"
fi

# Test 5: Try with invalid token
print_request "GET /api/bookings/my (with invalid token)"
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/bookings/my" \
  -H "Authorization: Bearer invalid.token.here")
if [ "$HTTP_STATUS" == "401" ] || [ "$HTTP_STATUS" == "403" ]; then
    echo -e "${GREEN}✓ Invalid token rejected${NC}"
    echo -e "Check logs for: ${YELLOW}[JwtService] Error extracting username${NC}"
else
    echo -e "${YELLOW}⚠ Expected 401/403 but got $HTTP_STATUS${NC}"
fi

echo ""
echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  Test Summary${NC}"
echo -e "${BLUE}========================================${NC}"
echo "1. Check logs at: logs/cinebook.log"
echo "2. For 403 error details, grep: grep '403' logs/cinebook.log"
echo "3. For JWT details, grep: grep '\[JWT\]' logs/cinebook.log"
echo "4. For auth failures, grep: grep '\[UserDetailsService\]' logs/cinebook.log"
echo ""
echo -e "${GREEN}Testing complete!${NC}"
echo "Check the documentation for more details:"
echo "  - QUICK_DEBUG_GUIDE.md"
echo "  - LOGGING_AND_DEBUGGING_GUIDE.md"

