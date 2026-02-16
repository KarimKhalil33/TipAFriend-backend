# 🏗️ TipAFriend Backend Architecture

## 📦 Package Structure (Skeleton)

```
com.tipafriend/
├── TipAFriendApiApplication.java          # Main Spring Boot entry point
│
├── config/                                 # Configuration classes
│   ├── SecurityConfig.java                # Security & JWT config
│   ├── CorsConfig.java                    # CORS for mobile app
│   └── OpenApiConfig.java                 # Swagger/API docs
│
├── domain/                                 # Domain entities (JPA)
│   ├── User.java
│   ├── Post.java
│   ├── Friendship.java
│   ├── FriendRequest.java
│   ├── TaskAssignment.java
│   ├── Payment.java
│   └── Review.java
│
├── repository/                             # Spring Data JPA repositories
│   ├── UserRepository.java
│   ├── PostRepository.java
│   ├── FriendshipRepository.java
│   ├── FriendRequestRepository.java
│   ├── TaskAssignmentRepository.java
│   ├── PaymentRepository.java
│   └── ReviewRepository.java
│
├── dto/                                    # Data Transfer Objects
│   ├── request/                           # API request DTOs
│   │   ├── RegisterRequest.java
│   │   ├── LoginRequest.java
│   │   ├── CreatePostRequest.java
│   │   └── ...
│   └── response/                          # API response DTOs
│       ├── AuthResponse.java
│       ├── UserResponse.java
│       ├── PostResponse.java
│       └── ...
│
├── service/                                # Business logic
│   ├── AuthService.java
│   ├── UserService.java
│   ├── FriendshipService.java
│   ├── PostService.java
│   ├── TaskService.java
│   ├── PaymentService.java
│   └── ReviewService.java
│
├── controller/                             # REST controllers
│   ├── HealthController.java             # ✅ Already created
│   ├── AuthController.java               # /api/auth/*
│   ├── UserController.java               # /api/users/*
│   ├── FriendController.java             # /api/friends/*
│   ├── PostController.java               # /api/posts/*
│   ├── TaskController.java               # /api/tasks/*
│   ├── PaymentController.java            # /api/payments/*
│   └── ReviewController.java             # /api/reviews/*
│
├── security/                               # Security components
│   ├── JwtTokenProvider.java             # Generate & validate JWT
│   ├── JwtAuthenticationFilter.java      # Filter for JWT
│   └── CustomUserDetailsService.java     # Load users
│
├── exception/                              # Custom exceptions & handlers
│   ├── GlobalExceptionHandler.java
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── BadRequestException.java
│
└── util/                                   # Utility classes
    └── ValidationUtil.java
```

---

## 🗄️ Database Schema (Core Tables)

### Users & Auth
- `users` - Core user accounts
- `user_settings` - User preferences (radius, availability)

### Friends System
- `friend_requests` - Pending friend requests
- `friendships` - Accepted friendships (symmetric)

### Posts & Tasks
- `posts` - Both Requests and Offers
- `post_media` - Images for posts
- `task_assignments` - Acceptance & lifecycle tracking

### Payments & Reviews
- `payments` - Stripe transactions
- `reviews` - Ratings after completion

### AI Features (Phase 2)
- `price_suggestions` - AI predictions
- `friend_match_scores` - AI rankings

---

## 🔐 Security Flow

```
Mobile App
    ↓
    POST /api/auth/login
    ↓
Spring Security Filter Chain
    ↓
AuthController → AuthService
    ↓
Generate JWT Token
    ↓
Return { token, user }
    ↓
Mobile stores token
    ↓
All future requests:
    Header: Authorization: Bearer <token>
    ↓
JwtAuthenticationFilter validates
    ↓
Sets SecurityContext
    ↓
Controller methods execute
```

---

## 🔄 Friends-Only Access Pattern

Every endpoint that deals with posts/tasks will:

```java
1. Get current authenticated user from SecurityContext
2. If viewing posts: filter by friends only
3. If accepting: verify accepter is friend of poster
4. If creating: post is visible only to friends
```

This is THE core differentiator of your app!

---

## 📊 API Endpoint Structure (MVP)

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`

### Users
- `GET /api/users/{id}`
- `PUT /api/users/profile`
- `GET /api/users/search?q=username`

### Friends
- `GET /api/friends`
- `GET /api/friends/requests`
- `POST /api/friends/requests`
- `PUT /api/friends/requests/{id}/accept`
- `PUT /api/friends/requests/{id}/decline`
- `DELETE /api/friends/{id}`

### Posts
- `GET /api/posts/feed` (friends only!)
- `GET /api/posts/{id}`
- `POST /api/posts`
- `PUT /api/posts/{id}`
- `DELETE /api/posts/{id}`

### Tasks
- `POST /api/posts/{id}/accept`
- `PUT /api/tasks/{id}/complete`
- `GET /api/tasks/my-tasks`

### Payments
- `POST /api/payments/intent`
- `POST /api/payments/confirm`

### Reviews
- `POST /api/tasks/{id}/review`
- `GET /api/users/{id}/reviews`

---

## 🚀 Development Phases

### Phase 1: Foundation (NOW)
- ✅ Database + Docker
- ✅ Health check
- ⬜ User domain + repository
- ⬜ JWT Authentication
- ⬜ User registration/login

### Phase 2: Friends System
- ⬜ Friend request flow
- ⬜ Friends list
- ⬜ Friends-only filtering

### Phase 3: Posts & Tasks
- ⬜ Create/view posts
- ⬜ Accept post (friends only!)
- ⬜ Task lifecycle

### Phase 4: Payments
- ⬜ Stripe integration
- ⬜ Payment flow

### Phase 5: Polish
- ⬜ Reviews
- ⬜ Notifications
- ⬜ AI placeholders

---

## 🎯 Key Implementation Rules

1. **Friends-Only Enforcement**: Always at service layer, never just UI
2. **DTO Pattern**: Never expose entities directly in API
3. **Validation**: Use `@Valid` and Jakarta validators
4. **Transactions**: Use `@Transactional` for multi-step operations
5. **Error Handling**: Consistent error response format
6. **Testing**: Repository → Service → Controller (test each layer)

---

## 🔧 Tech Stack Recap

- **Runtime**: Java 17, Spring Boot 4.0.2
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL 16
- **Migrations**: Flyway
- **Testing**: JUnit 5, Testcontainers
- **Documentation**: OpenAPI/Swagger (to add)

---

Ready to build this? 🚀

