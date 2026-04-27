# 🔒 HTTPS Setup for Local Development

## Problem
Your frontend runs on `https://localhost:3000` but backend is `http://localhost:8080/api`, causing **mixed content blocking** - browsers won't allow HTTPS pages to call HTTP APIs.

## Solution
Enable HTTPS on the backend so everything is secure end-to-end.

---

## Step 1: Generate Self-Signed Certificate

Run the certificate generation script:

```bash
chmod +x generate-cert.sh
./generate-cert.sh
```

**Expected output:**
```
🔐 Generating self-signed certificate for localhost...
✅ Certificate generated successfully!
📁 Location: ./keystore/dev-keystore.p12
🔑 Keystore password: tipafriend-dev-password
```

This creates `keystore/dev-keystore.p12` with a self-signed certificate valid for 365 days.

---

## Step 2: Enable HTTPS in application.properties

Uncomment these lines in `src/main/resources/application.properties`:

```properties
# HTTPS Configuration (optional for local development)
server.ssl.enabled=true
server.ssl.key-store=keystore/dev-keystore.p12
server.ssl.key-store-password=tipafriend-dev-password
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=localhost
server.port=8443
```

**Note:** This changes the port from 8080 to 8443 (standard HTTPS port).

---

## Step 3: Update Frontend API URL

In your frontend `.env.local`, update:

```env
# From:
NEXT_PUBLIC_API_URL=http://localhost:8080/api

# To:
NEXT_PUBLIC_API_URL=https://localhost:8443/api
```

---

## Step 4: Start Backend with HTTPS

```bash
./mvnw spring-boot:run
```

**Expected output:**
```
🔒 HTTPS enabled for local development on port 8443
Tomcat started on port(s): 8443 (https)
```

---

## Step 5: Test HTTPS Connection

```bash
# Get token (ignore certificate warning)
curl -k -X POST https://localhost:8443/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"karim","password":"KarimKhalil"}' | jq '.token'

# Test payment (with token)
TOKEN=$(curl -sk -X POST https://localhost:8443/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"karim","password":"KarimKhalil"}' | jq -r '.token')

curl -sk -X POST https://localhost:8443/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"postId": 1, "payeeId": 2, "amount": 50}' | jq '.'
```

**Note:** The `-k` flag tells curl to ignore SSL certificate verification (needed for self-signed cert).

---

## Step 6: Frontend Dev with HTTPS

Start your frontend:

```bash
npm run dev:https
```

Frontend will be at `https://localhost:3000`

---

## Expected Flow

```
Frontend (HTTPS)           Backend (HTTPS)
https://localhost:3000 ──→ https://localhost:8443/api ✅ WORKS
```

No more mixed content errors! 🎉

---

## Security Notes

- ✅ Self-signed certificate is fine for **local development only**
- ✅ Stripe.js still communicates directly with Stripe over HTTPS
- ✅ Your app never handles raw card numbers
- ⚠️  Browser will show security warning (normal for self-signed certs)
- ⚠️  Never use self-signed certs in production (use Let's Encrypt or similar)

---

## Troubleshooting

### Certificate not found
```
Error: Cannot find keystore/dev-keystore.p12
```
→ Run `./generate-cert.sh` first

### Port 8443 already in use
```
Address already in use: bind
```
→ Change port in `application.properties` or kill process: `lsof -i :8443`

### Mixed content still blocked
```
Mixed Content: The page was loaded over HTTPS, but requested an insecure resource
```
→ Verify frontend is using `https://localhost:8443/api` (not `http://`)

### Browser SSL warning
```
Your connection is not private
```
→ This is normal for self-signed certs in development. Click "Advanced" → "Proceed"

---

## How to Disable HTTPS Later

Comment out the SSL lines in `application.properties`:

```properties
# server.ssl.enabled=true
# server.ssl.key-store=keystore/dev-keystore.p12
# ... etc
server.port=8080  # Back to HTTP port
```

Restart backend and update frontend to `http://localhost:8080/api`

---

## Summary

| Step | Command |
|------|---------|
| 1. Generate cert | `./generate-cert.sh` |
| 2. Enable HTTPS | Uncomment lines in `application.properties` |
| 3. Update frontend URL | Set `NEXT_PUBLIC_API_URL=https://localhost:8443/api` |
| 4. Start backend | `./mvnw spring-boot:run` |
| 5. Start frontend | `npm run dev:https` |

---

**Status:** ✅ HTTPS Ready  
**Certificate:** ✅ Self-signed (dev only)  
**Next:** Run the steps above!

