# 🗄️ Database Migration Created

## ✅ V2__create_core_tables.sql

This Flyway migration creates all 7 tables for TipAFriend:

### Tables Created:

1. **users** - Extended with new columns
   - Added: `display_name`, `photo_url`, `bio`, `phone_number`
   - Added: `email_verified`, `phone_verified`
   - Added: `latitude`, `longitude`, `default_radius_km`
   - Added: `is_available`
   - Added: `stripe_customer_id`, `stripe_account_id`

2. **posts** - Main feature table
   - Request/Offer posts with category, location, price
   - Status lifecycle tracking
   - Indexed for fast feed queries

3. **friend_requests** - Pending invitations
   - Unique constraint on (from_user, to_user)
   - Status: PENDING/ACCEPTED/DECLINED

4. **friendships** - Active friendships
   - Bidirectional relationships
   - Unique constraint on (user, friend)

5. **task_assignments** - Accepted tasks
   - One-to-one with posts
   - Tracks completion status

6. **payments** - Stripe payments
   - All Stripe IDs stored
   - Platform fee tracking

7. **reviews** - Post-task ratings
   - 1-5 star ratings
   - Unique: one review per user per task

### Indexes Created:

- **Performance indexes** for feed queries, friend lookups, Stripe webhooks
- **Foreign key indexes** for all relationships
- **Composite indexes** for common query patterns

### Triggers Created:

- Auto-update `updated_at` on users, posts, and payments

---

## 🚀 To Apply Migration:

```bash
# Restart Spring Boot
./mvnw spring-boot:run
```

Flyway will automatically:
1. Detect the new V2 migration
2. Run it against PostgreSQL
3. Update flyway_schema_history table

---

## ✅ Verify Migration:

```bash
# Connect to database
docker exec -it tipafriend-postgres psql -U tipafriend -d tipafriend_db

# List all tables
\dt

# Should see:
# - users
# - posts
# - friend_requests
# - friendships
# - task_assignments
# - payments
# - reviews
# - flyway_schema_history
```

---

## 📝 Next Steps After Migration:

1. **Repositories** - Spring Data JPA interfaces
2. **Services** - Business logic layer
3. **DTOs** - Request/Response objects
4. **Controllers** - REST API endpoints
5. **Security** - JWT authentication

---

**Migration is ready! Restart Spring Boot to apply it.** 🎉

