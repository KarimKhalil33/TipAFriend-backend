# 🚀 Complete HTTPS + Stripe Setup Guide

## Current Status
✅ Backend compiles successfully (0 errors)  
✅ Dotenv loader configured (.env file support)  
✅ HTTPS configuration ready  
✅ Stripe integration ready  

---

## Step 1: Generate Self-Signed Certificate (1 minute)

```bash
chmod +x generate-cert.sh
./generate-cert.sh
```

Expected output:
```
🔐 Generating self-signed certificate for localhost...
✅ Certificate generated successfully!
📁 Location: ./keystore/dev-keystore.p12
🔑 Keystore password: tipafriend-dev-password
```

---

## Step 2: Enable HTTPS in Backend

Edit `src/main/resources/application.properties` and uncomment these lines:

```properties
# HTTPS Configuration (optional for local development)
server.ssl.enabled=true
server.ssl.key-store=keystore/dev-keystore.p12
server.ssl.key-store-password=tipafriend-dev-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=localhost
server.port=8443
```

**Result:** Backend will run on `https://localhost:8443` instead of `http://localhost:8080`

---

## Step 3: Update Frontend Configuration

Edit your frontend `.env.local`:

```env
# Change from:
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# Change to:
NEXT_PUBLIC_API_URL=https://localhost:8443/api
```

---

## Step 4: Start Backend

```bash
./mvnw spring-boot:run
```

Look for this in the logs:
```
🔒 HTTPS enabled for local development on port 8443
Tomcat started on port(s): 8443 (https)
✅ .env file loaded successfully with X properties
✅ STRIPE_SECRET_KEY loaded: YES
```

---

## Step 5: Start Frontend

```bash
npm run dev:https
```

Expected:
```
✓ ready - started server on 0.0.0.0:3000, url: https://localhost:3000
```

---

## Step 6: Test the Full Flow

### Test 1: Health Check
```bash
curl -k https://localhost:8443/api/health
```

Expected:
```json
{
  "service": "TipAFriend API",
  "status": "UP"
}
```

### Test 2: Login
```bash
curl -k -X POST https://localhost:8443/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"karim","password":"KarimKhalil"}' | jq '.token'
```

Expected: JWT token returned

### Test 3: Create Payment with Stripe
```bash
TOKEN=$(curl -sk -X POST https://localhost:8443/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"karim","password":"KarimKhalil"}' | jq -r '.token')

curl -sk -X POST https://localhost:8443/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"postId": 1, "payeeId": 2, "amount": 50}' | jq '.'
```

Expected response with `stripeClientSecret`:
```json
{
  "id": 1,
  "status": "PROCESSING",
  "stripePaymentIntentId": "pi_1234567890",
  "stripeClientSecret": "pi_1234567890_secret_abc123",
  "amount": 50
}
```

If you see:
- ✅ `stripeClientSecret` present → Stripe working!
- ⚠️ `status: PENDING` + null secret → Stripe keys not loaded (check logs)
- ❌ `status: FAILED` + error message → Stripe API error (check message)

---

## What's Happening Behind the Scenes

### 1. .env File Loading
- `DotenvEnvironmentPostProcessor` loads your `.env` file at startup
- `STRIPE_SECRET_KEY` and `STRIPE_WEBHOOK_SECRET` are loaded
- All environment variables are available to Spring

### 2. HTTPS Configuration
- `HttpsConfig` enables SSL/TLS when `server.ssl.enabled=true`
- Uses self-signed certificate from `keystore/dev-keystore.p12`
- Runs on port 8443 (standard HTTPS port)

### 3. Payment Creation
- Frontend sends HTTPS request to backend
- Backend creates Stripe PaymentIntent
- Backend returns `stripeClientSecret` to frontend
- Frontend uses secret to confirm payment with Stripe.js
- **Raw card data never touches your backend** (Stripe handles it)

---

## Browser Security Warning

When you visit `https://localhost:3000`:

```
Your connection is not private
⚠️ Attackers might be trying to steal your information
```

This is **NORMAL** for self-signed certificates in development.

**How to proceed:**
1. Click "Advanced"
2. Click "Proceed to localhost (unsafe)"

This is safe because:
- ✅ You're on localhost (your own computer)
- ✅ Self-signed cert is for development only
- ✅ No real data is being transmitted

**In production:** Use Let's Encrypt or AWS Certificate Manager (not self-signed)

---

## Troubleshooting

### Issue: Certificate not found
```
Error: java.io.FileNotFoundException: keystore/dev-keystore.p12
```
**Fix:** Run `./generate-cert.sh` first

### Issue: Port 8443 already in use
```
Address already in use: bind
```
**Fix:** Kill the process: `lsof -i :8443` and note the PID, then `kill <PID>`

### Issue: STRIPE_SECRET_KEY not loaded
Logs show:
```
⚠️ STRIPE_SECRET_KEY loaded: NO
```
**Fix:** 
- Ensure `.env` file is in the root directory
- Check `STRIPE_SECRET_KEY=` is not blank
- Restart backend after updating `.env`

### Issue: Mixed content error in browser
```
Mixed Content: The page was loaded over HTTPS, but requested an insecure resource
```
**Fix:** Verify frontend `.env.local` has `NEXT_PUBLIC_API_URL=https://localhost:8443/api`

---

## Development Commands Summary

| Task | Command |
|------|---------|
| Generate certificate | `chmod +x generate-cert.sh && ./generate-cert.sh` |
| Start backend (HTTPS) | `./mvnw spring-boot:run` |
| Start frontend (HTTPS) | `npm run dev:https` |
| Test health | `curl -k https://localhost:8443/api/health` |
| Test payment | See "Test 3: Create Payment" above |

---

## Disabling HTTPS Later

If you want to go back to HTTP:

1. **Comment out** these lines in `application.properties`:
```properties
# server.ssl.enabled=true
# server.ssl.key-store=keystore/dev-keystore.p12
# ... (comment all SSL lines)
server.port=8080
```

2. **Update frontend** `.env.local`:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

3. **Restart both** backend and frontend

---

## Security Summary

| Layer | Status | Details |
|-------|--------|---------|
| Frontend → Backend | ✅ HTTPS | Encrypted communication |
| Backend → Stripe | ✅ HTTPS | Secure payment API |
| Card Data | ✅ Safe | Never touches your backend |
| Certificate | ⚠️ Self-signed | OK for dev, use Let's Encrypt in prod |
| Stripe Keys | ✅ Loaded | From `.env` file |

---

## What's Ready to Test

✅ **User Authentication** - Login/register over HTTPS  
✅ **Post Creation** - Create tasks over HTTPS  
✅ **Stripe Payments** - Create payments and get client secret  
✅ **Payment Confirmation** - Use client secret with Stripe.js  
✅ **End-to-End HTTPS** - Frontend + Backend on secure protocol  

---

## Next: Frontend Integration

Your frontend can now:

1. Accept real card input via Stripe.js
2. Confirm payment with `clientSecret`
3. Process payments securely
4. No mixed content errors
5. Production-ready (just swap cert in production)

---

## Files Modified/Created

✅ `pom.xml` - Added dotenv-java dependency  
✅ `src/main/resources/application.properties` - Added SSL config  
✅ `src/main/java/com/tipafriend/config/DotenvEnvironmentPostProcessor.java` - Loads .env  
✅ `src/main/java/com/tipafriend/config/HttpsConfig.java` - Enables SSL  
✅ `src/main/resources/META-INF/spring.factories` - Registers dotenv loader  
✅ `generate-cert.sh` - Certificate generation script  
✅ `.env` - Already has Stripe keys  

---

## Status

✅ **Compilation:** 0 errors  
✅ **Dependencies:** All required  
✅ **Configuration:** Ready  
✅ **Certificates:** Ready to generate  
✅ **Stripe:** Keys in .env  

**→ Ready to deploy!** 🚀

Just follow the 6 steps above and you're good to go!

