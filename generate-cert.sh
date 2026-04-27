#!/bin/bash

# Generate self-signed certificate for local HTTPS development
# This creates a keystore that Spring Boot can use

set -e

KEYSTORE_DIR="./keystore"
KEYSTORE_FILE="$KEYSTORE_DIR/dev-keystore.p12"
KEYSTORE_PASSWORD="tipafriend-dev-password"
ALIAS="localhost"
VALIDITY_DAYS=365

# Create keystore directory if it doesn't exist
mkdir -p "$KEYSTORE_DIR"

# Check if keystore already exists
if [ -f "$KEYSTORE_FILE" ]; then
    echo "✅ Keystore already exists at $KEYSTORE_FILE"
    exit 0
fi

echo "🔐 Generating self-signed certificate for localhost..."

# Generate the keystore with self-signed certificate
keytool -genkeypair \
    -alias "$ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -keystore "$KEYSTORE_FILE" \
    -storepass "$KEYSTORE_PASSWORD" \
    -validity "$VALIDITY_DAYS" \
    -dname "CN=localhost, OU=Development, O=TipAFriend, L=Local, C=US" \
    -ext SAN=dns:localhost,dns:*.localhost,ip:127.0.0.1

echo "✅ Certificate generated successfully!"
echo "📁 Location: $KEYSTORE_FILE"
echo "🔑 Keystore password: $KEYSTORE_PASSWORD"
echo ""
echo "The certificate is valid for $VALIDITY_DAYS days."
echo "Note: This is a self-signed certificate for development only."
echo "Your browser may show a security warning - this is normal and expected."

