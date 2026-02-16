# TipAFriend Backend API

Friends-only task marketplace backend built with Spring Boot.

---

## 🚀 Quick Start

### Prerequisites
- Java 17+
- Docker Desktop
- Maven (or use included `./mvnw`)

### 0. Environment Setup (First Time Only)

Copy the environment template and configure your credentials:

```bash
cp .env.example .env
```

Edit `.env` with your values (these will NOT be committed):
```env
DB_NAME=tipafriend_db
DB_USER=tipafriend
DB_PASSWORD=your_secure_password
```

> 📖 **Security Note**: Credentials are managed via environment variables. See `SECURITY.md` for details.

### 1. Start PostgreSQL

```bash
docker-compose up -d
```

### 2. Run the Application

```bash
./mvnw spring-boot:run
```

### 3. Verify It's Running

```bash
curl http://localhost:8080/api/health
```

Expected response:
```json
{
  "status": "UP",
  "service": "TipAFriend API",
  "message": "Backend is running successfully! 🚀"
}
```

---

## 📊 Configuration

### Database
- **Host**: localhost:5432
- **Database**: tipafriend_db
- **Username**: tipafriend
- **Password**: tipafriend

### Application
- **Port**: 8080
- **Health Endpoint**: http://localhost:8080/api/health
- **Actuator**: http://localhost:8080/actuator/health

---

## 🛠 Useful Commands

### Stop the database:
```bash
docker-compose down
```

### View database logs:
```bash
docker-compose logs -f postgres
```

### Connect to PostgreSQL:
```bash
docker exec -it tipafriend-postgres psql -U tipafriend -d tipafriend_db
```

### Restart everything:
```bash
docker-compose restart
./mvnw spring-boot:run
```

---

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/tipafriend/
│   │   ├── TipAFriendApiApplication.java
│   │   ├── config/
│   │   │   └── SecurityConfig.java
│   │   └── controller/
│   │       └── HealthController.java
│   └── resources/
│       ├── application.properties
│       └── db/migration/
│           └── V1__create_users_table.sql
```

---

## 🐛 Troubleshooting

### Port 5432 already in use?
If you have PostgreSQL installed via Homebrew, stop it:
```bash
brew services stop postgresql
```

### Application won't start?
- Verify PostgreSQL is running: `docker-compose ps`
- Check logs: `docker-compose logs postgres`
- Ensure port 8080 is available: `lsof -i :8080`

---

## 🔧 Tech Stack

- **Java 17** with **Spring Boot 4.0.2**
- **PostgreSQL 16** (Docker)
- **Spring Security** + JWT (to be configured)
- **Flyway** for database migrations
- **Spring Data JPA** + Hibernate
- **Maven** for build management

---

## 📚 Next Steps

See `ARCHITECTURE.md` for the full backend design and planned features.


