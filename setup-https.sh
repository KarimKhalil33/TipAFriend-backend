#!/bin/bash

# Quick HTTPS Setup Script for TipAFriend Backend + Frontend
# This script automates certificate generation and configuration

set -e

echo "🔒 TipAFriend HTTPS Setup"
echo "========================="
echo ""

# Step 1: Generate certificate
echo "📋 Step 1: Generating self-signed certificate..."
if [ -f "keystore/dev-keystore.p12" ]; then
    echo "✅ Certificate already exists"
else
    chmod +x generate-cert.sh
    ./generate-cert.sh
fi

echo ""
echo "✅ Setup Complete!"
echo ""
echo "📝 Next Steps:"
echo ""
echo "1️⃣  Uncomment HTTPS lines in src/main/resources/application.properties:"
echo "   - Uncomment: server.ssl.enabled=true"
echo "   - Uncomment: server.ssl.key-store=keystore/dev-keystore.p12"
echo "   - Uncomment: server.ssl.key-store-password=tipafriend-dev-password"
echo "   - Uncomment: server.ssl.key-store-type=PKCS12"
echo "   - Uncomment: server.ssl.key-alias=localhost"
echo "   - Uncomment: server.port=8443"
echo ""
echo "2️⃣  Update your frontend .env.local:"
echo "   NEXT_PUBLIC_API_URL=https://localhost:8443/api"
echo ""
echo "3️⃣  Start the backend:"
echo "   ./mvnw spring-boot:run"
echo ""
echo "4️⃣  Start the frontend:"
echo "   npm run dev:https"
echo ""
echo "5️⃣  Access at: https://localhost:3000"
echo ""
echo "🔓 To disable HTTPS later, comment out the SSL lines in application.properties"
echo ""

