# 🔒 Security & Environment Variables

## Important: Credentials Management

This project uses **environment variables** to keep sensitive credentials out of version control.

---

## ✅ What's Protected

The following files are in `.gitignore` and **will NOT be committed**:
- ✅ `.env` - Your local environment variables
- ✅ `.env.local` - Local overrides
- ✅ `*.env` - Any environment files
- ✅ `application-local.properties` - Local config overrides

The following **WILL be committed** safely:
- ✅ `.env.example` - Template with placeholder values
- ✅ `application.properties` - Uses `${VARIABLE}` syntax (no hardcoded credentials)
- ✅ `docker-compose.yml` - Uses `${VARIABLE}` syntax (no hardcoded credentials)

---

## 🚀 Setup for New Developers

1. **Copy the example file:**
   ```bash
   cp .env.example .env
   ```

2. **Edit `.env` with your actual values:**
   ```bash
   # Database Configuration
   DB_NAME=tipafriend_db
   DB_USER=tipafriend
   DB_PASSWORD=your_secure_password_here
   DB_HOST=localhost
   DB_PORT=5432
   
   # Security
   ADMIN_USERNAME=admin
   ADMIN_PASSWORD=your_admin_password_here
   ```

3. **Start the application:**
   ```bash
   docker-compose up -d
   ./mvnw spring-boot:run
   ```

---

## 📝 How It Works

### application.properties
Uses Spring Boot's environment variable syntax with defaults:
```properties
spring.datasource.username=${DB_USER:tipafriend}
spring.datasource.password=${DB_PASSWORD:tipafriend}
```
- `${DB_USER}` - Reads from environment variable
- `:tipafriend` - Fallback default if not set

### docker-compose.yml
Uses Docker Compose environment variable syntax:
```yaml
environment:
  POSTGRES_USER: ${DB_USER:-tipafriend}
  POSTGRES_PASSWORD: ${DB_PASSWORD:-tipafriend}
```
- Docker Compose automatically reads from `.env` file
- `:-tipafriend` - Fallback default if not set

---

## 🔐 Production Deployment

For production, **NEVER commit real credentials**. Instead:

### Option 1: Environment Variables (Recommended)
Set environment variables on your deployment platform:
- Render: Settings → Environment Variables
- Heroku: Settings → Config Vars
- AWS: Parameter Store / Secrets Manager
- Docker: Pass `-e` flags or use secrets

### Option 2: Secret Management
Use proper secret management:
- AWS Secrets Manager
- HashiCorp Vault
- Kubernetes Secrets
- Docker Secrets

---

## ⚠️ Before Committing

Always check you're not committing secrets:

```bash
# Check what will be committed
git status

# Search for potential secrets
grep -r "password.*=" --exclude-dir=target --exclude-dir=.git .

# Verify .env is ignored
git check-ignore .env
# Should output: .env
```

---

## 🚨 If You Accidentally Commit Secrets

1. **Immediately rotate the credentials** (change passwords)
2. **Remove from Git history:**
   ```bash
   git filter-branch --force --index-filter \
   "git rm --cached --ignore-unmatch .env" \
   --prune-empty --tag-name-filter cat -- --all
   ```
3. **Force push** (⚠️ dangerous):
   ```bash
   git push origin --force --all
   ```

---

## ✅ Safe to Commit

These contain **NO secrets**:
- ✅ `.env.example` - Template only
- ✅ `application.properties` - Uses `${VARIABLES}`
- ✅ `docker-compose.yml` - Uses `${VARIABLES}`
- ✅ All source code files
- ✅ Documentation files

---

**Your credentials are now secure!** 🔒

