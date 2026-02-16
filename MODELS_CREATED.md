# 📊 Domain Models Created

## ✅ Complete Entity Model for TipAFriend

### Core Entities (7 total)

#### 1. **User** 👤
Core user entity with authentication and profile data.

**Fields:**
- Authentication: `email`, `username`, `passwordHash`
- Profile: `displayName`, `photoUrl`, `bio`, `phoneNumber`
- Verification: `emailVerified`, `phoneVerified`
- Location: `latitude`, `longitude`, `defaultRadiusKm`
- Availability: `isAvailable`
- Stripe: `stripeCustomerId`, `stripeAccountId`
- Audit: `createdAt`, `updatedAt`

---

#### 2. **Post** 📝
Main feature - Users create Requests (need help) or Offers (willing to help).

**Fields:**
- Core: `author`, `type` (REQUEST/OFFER), `title`, `description`, `category`
- Location: `locationName`, `latitude`, `longitude`
- Timing: `scheduledTime`, `durationMinutes`
- Payment: `paymentType` (FIXED/HOURLY), `price`
- Status: `status` (OPEN → ACCEPTED → IN_PROGRESS → COMPLETED → REVIEWED)
- Audit: `createdAt`, `updatedAt`

**Key Business Rule:** Only friends can see each other's posts!

---

#### 3. **FriendRequest** 🤝
Pending friend invitations.

**Fields:**
- `fromUser`, `toUser`
- `status` (PENDING/ACCEPTED/DECLINED)
- `createdAt`, `respondedAt`

**Constraint:** Unique constraint on (fromUser, toUser) - can't send duplicate requests

---

#### 4. **Friendship** 👥
Accepted friendships (bidirectional relationship).

**Fields:**
- `user`, `friend`
- `createdAt`

**Note:** When a friend request is accepted, create TWO friendship rows:
- (userA, userB)
- (userB, userA)

This makes queries simpler!

---

#### 5. **TaskAssignment** ✅
When someone accepts a post, this tracks the task lifecycle.

**Fields:**
- `post` (one-to-one), `accepter`
- `status` (matches Post status)
- `acceptedAt`, `completedAt`, `cancelledAt`
- `cancellationReason`

**Business Rule:** One accepter per post (MVP)

---

#### 6. **Payment** 💰
Stripe payment tracking.

**Fields:**
- `post`, `payer`, `payee`, `amount`
- `status` (PENDING → PROCESSING → SUCCEEDED/FAILED)
- Stripe IDs: `stripePaymentIntentId`, `stripeChargeId`, `stripeTransferId`
- `platformFee`, `errorMessage`
- `createdAt`, `updatedAt`, `paidAt`

**Security:** Only store Stripe IDs, never raw card data!

---

#### 7. **Review** ⭐
Post-task reviews (both parties review each other).

**Fields:**
- `taskAssignment`, `reviewer`, `reviewee`
- `rating` (1-5 stars), `comment`
- `createdAt`

**Constraint:** Each user can only review once per task

---

## 📋 Enums Created

### PostType
- `REQUEST` - User needs help
- `OFFER` - User willing to help

### PostStatus  
- `OPEN` - Available for acceptance
- `ACCEPTED` - Someone accepted
- `IN_PROGRESS` - Work happening
- `COMPLETED` - Task done
- `REVIEWED` - Both reviewed
- `CANCELLED` - Cancelled

### PostCategory
- `CLEANING`, `MOVING`, `DELIVERY`, `TUTORING`, `TECH_SUPPORT`
- `HANDYMAN`, `PET_CARE`, `GARDENING`, `COOKING`
- `TRANSPORTATION`, `ERRANDS`, `OTHER`

### PaymentType
- `FIXED` - Fixed price
- `HOURLY` - Hourly rate

### FriendRequestStatus
- `PENDING`, `ACCEPTED`, `DECLINED`

### PaymentStatus
- `PENDING`, `PROCESSING`, `SUCCEEDED`, `FAILED`, `REFUNDED`

---

## 🔗 Entity Relationships

```
User
├── Posts (author) - One-to-Many
├── FriendRequests (sent/received) - One-to-Many
├── Friendships - Many-to-Many
├── TaskAssignments (accepter) - One-to-Many
├── Payments (payer/payee) - One-to-Many
└── Reviews (reviewer/reviewee) - One-to-Many

Post
├── Author (User) - Many-to-One
├── TaskAssignment - One-to-One (when accepted)
├── Payments - One-to-Many
└── Status lifecycle → affects visibility

FriendRequest
├── FromUser - Many-to-One
└── ToUser - Many-to-One

Friendship
├── User - Many-to-One
└── Friend - Many-to-One

TaskAssignment
├── Post - One-to-One
├── Accepter (User) - Many-to-One
├── Reviews - One-to-Many
└── Payments - One-to-Many

Payment
├── Post - Many-to-One
├── Payer (User) - Many-to-One
├── Payee (User) - Many-to-One
└── Stripe integration fields

Review
├── TaskAssignment - Many-to-One
├── Reviewer (User) - Many-to-One
└── Reviewee (User) - Many-to-One
```

---

## 🎯 Key Design Decisions

### 1. **Friends-Only Visibility**
- Enforced at service layer (not database)
- Feed query: `SELECT posts WHERE author_id IN (SELECT friend_id FROM friendships WHERE user_id = ?)`

### 2. **Bidirectional Friendships**
- Create two rows when friend request accepted
- Simplifies queries (no need for `OR` conditions)

### 3. **Post Status Lifecycle**
```
OPEN → ACCEPTED → IN_PROGRESS → COMPLETED → REVIEWED
         ↓
    CANCELLED (any time)
```

### 4. **One Accepter Per Post (MVP)**
- `TaskAssignment.post` is `@OneToOne`
- Can be changed to One-to-Many later

### 5. **Stripe Integration**
- Store only Stripe IDs
- Never store raw card data
- Platform fee calculation ready

### 6. **Indexes for Performance**
- `posts(author_id, created_at)` - Feed queries
- `posts(status)` - Filter by status
- `friendships(user_id)` - Friend lookups
- `friend_requests(to_user_id, status)` - Pending requests
- `payments(stripe_payment_intent_id)` - Stripe webhooks

---

## 📝 Next Steps

Now that models are created, we need:

1. **Flyway Migrations** - Create database tables
2. **Repositories** - Spring Data JPA interfaces
3. **Services** - Business logic + friends-only enforcement
4. **DTOs** - Request/Response objects
5. **Controllers** - REST endpoints
6. **Security** - JWT authentication

---

**All models are ready and follow Spring Boot / JPA best practices!** 🎉

