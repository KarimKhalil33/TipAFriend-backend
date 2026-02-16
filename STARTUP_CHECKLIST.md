# 🚦 Startup Checklist

Quick verification steps to ensure everything is working.

---

## Step 1: Start PostgreSQL

```bash
docker-compose up -d
```

**Expected Output:**
```
✅ Container tipafriend-postgres  Started
```

**Verify:**
```bash
docker-compose ps
```

---

## Step 2: Run the Application

```bash
./mvnw spring-boot:run
```

**Expected Output:**
```
✅ HikariPool-1 - Start completed
✅ Flyway migration V1__create_users_table.sql SUCCESS
✅ Started TipAFriendApiApplication in X seconds
```

---

## Step 3: Test the Health Endpoint

### Using curl:
```bash
curl http://localhost:8080/api/health
```

### Using browser:
http://localhost:8080/api/health

**Expected Response:**
```json
{
  "status": "UP",
  "service": "TipAFriend API",
  "timestamp": "2026-02-16T...",
  "message": "Backend is running successfully! 🚀"
}
```

---

## Step 4: Verify Database

Connect to PostgreSQL:
```bash
docker exec -it tipafriend-postgres psql -U tipafriend -d tipafriend_db
```

Check tables:
```sql
\dt
```

**Expected Output:**
```
              List of relations
 Schema |         Name          | Type  |   Owner
--------+-----------------------+-------+------------
 public | flyway_schema_history | table | tipafriend
 public | users                 | table | tipafriend
```

Exit:
```sql
\q
```

---

## ✅ Success Criteria

- ✅ PostgreSQL container running
- ✅ Application started without errors
- ✅ Health endpoint returns JSON with status "UP"
- ✅ Users table exists in database

**If all checks pass, you're ready to build the backend skeleton!** 🎉

---

## 🛑 Troubleshooting

### PostgreSQL won't start
```bash
docker-compose logs postgres
lsof -i :5432  # Check if port is in use
```

### Application won't start
- Verify PostgreSQL is running: `docker-compose ps`
- Check application.properties
- If you have Homebrew PostgreSQL: `brew services stop postgresql`

### Health endpoint returns 404
- Verify application started successfully
- Check URL: http://localhost:8080/api/health

---

**All checks passed? Let's build the skeleton!** 🚀


