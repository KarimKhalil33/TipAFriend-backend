# TipAFriend Backend API - Extended Endpoints Documentation

## Base URL
`http://localhost:8080/api`

---

## 📋 Table of Contents
1. [Authentication](#authentication)
2. [Conversations](#conversations)
3. [Payments](#payments)
4. [Reviews](#reviews)
5. [Error Handling](#error-handling)

---

## Authentication

All endpoints (except `/auth/login` and `/auth/register`) require a Bearer token in the Authorization header:

```
Authorization: Bearer <token>
```

---

## Conversations

### 1. Create Conversation
**POST** `/conversations`

Creates a new conversation. For DIRECT conversations, automatically adds the current user if not included.

**Request Body:**
```json
{
  "type": "DIRECT",
  "taskAssignmentId": null,
  "participantIds": [2]
}
```

**Response:**
```json
{
  "id": 5
}
```

**Notes:**
- For DIRECT: Pass an array with one other user's ID
- For TASK_THREAD: Must include both author and accepter IDs
- Current user is automatically added to DIRECT conversations

---

### 2. Get or Create Conversation (NEW - Idempotent)
**POST** `/conversations/get-or-create`

Gets an existing conversation or creates one. **Idempotent** - safe to call multiple times.

**Request Body:**
```json
{
  "type": "DIRECT",
  "taskAssignmentId": null,
  "participantIds": [2]
}
```

**Response:**
```json
{
  "id": 5,
  "type": "DIRECT",
  "taskAssignmentId": null,
  "participants": [
    {
      "id": 1,
      "username": "karim",
      "displayName": "Karim",
      "photoUrl": null,
      "bio": null
    },
    {
      "id": 2,
      "username": "test",
      "displayName": "Test User",
      "photoUrl": null,
      "bio": null
    }
  ],
  "lastMessage": {
    "id": 10,
    "conversationId": 5,
    "senderId": 1,
    "body": "Hello!",
    "createdAt": "2026-02-17T20:30:00"
  },
  "unreadCount": 0,
  "createdAt": "2026-02-17T20:00:00"
}
```

---

### 3. List Conversations (NEW - Inbox)
**GET** `/conversations`

Lists all conversations for the authenticated user, sorted by most recent first.

**Response:**
```json
[
  {
    "id": 5,
    "type": "DIRECT",
    "taskAssignmentId": null,
    "participants": [
      {
        "id": 1,
        "username": "karim",
        "displayName": "Karim",
        "photoUrl": null,
        "bio": null
      },
      {
        "id": 2,
        "username": "test",
        "displayName": "Test User",
        "photoUrl": null,
        "bio": null
      }
    ],
    "lastMessage": {
      "id": 10,
      "conversationId": 5,
      "senderId": 1,
      "body": "Hello!",
      "createdAt": "2026-02-17T20:30:00"
    },
    "unreadCount": 0,
    "createdAt": "2026-02-17T20:00:00"
  }
]
```

---

### 4. Get Messages in Conversation
**GET** `/conversations/{conversationId}/messages`

Gets all messages in a conversation (requires participation).

**Response:**
```json
[
  {
    "id": 1,
    "conversationId": 5,
    "senderId": 1,
    "body": "Hello!",
    "createdAt": "2026-02-17T20:30:00"
  },
  {
    "id": 2,
    "conversationId": 5,
    "senderId": 2,
    "body": "Hi there!",
    "createdAt": "2026-02-17T20:31:00"
  }
]
```

---

### 5. Send Message
**POST** `/conversations/messages`

Sends a message in a conversation. Participants are automatically notified.

**Request Body:**
```json
{
  "conversationId": 5,
  "body": "Thanks for helping!"
}
```

**Response:**
```json
{
  "id": 11
}
```

---

## Payments

### 1. Create Payment
**POST** `/payments`

Creates a payment record. If Stripe is configured, creates a PaymentIntent automatically.

**Request Body:**
```json
{
  "postId": 10,
  "payeeId": 2,
  "amount": 50.00,
  "stripePaymentIntentId": null
}
```

**Response:**
```json
{
  "id": 1,
  "postId": 10,
  "payerId": 1,
  "payeeId": 2,
  "amount": 50.00,
  "status": "PROCESSING",
  "stripePaymentIntentId": "pi_1234567890",
  "stripeClientSecret": "pi_1234567890_secret_abcdefg",
  "errorMessage": null,
  "createdAt": "2026-02-17T20:30:00",
  "paidAt": null
}
```

**Frontend Flow:**
1. Call this endpoint to get `stripeClientSecret`
2. Use Stripe.js to confirm the payment with the secret
3. When payment succeeds, backend webhook updates status automatically

---

### 2. Update Payment Status
**PUT** `/payments/{paymentId}/status`

Updates payment status (usually called after Stripe webhook or manual confirmation).

**Request Body:**
```json
{
  "status": "SUCCEEDED",
  "errorMessage": null
}
```

**Response:**
```json
{
  "id": 1,
  "postId": 10,
  "payerId": 1,
  "payeeId": 2,
  "amount": 50.00,
  "status": "SUCCEEDED",
  "stripePaymentIntentId": "pi_1234567890",
  "stripeClientSecret": "pi_1234567890_secret_abcdefg",
  "errorMessage": null,
  "createdAt": "2026-02-17T20:30:00",
  "paidAt": "2026-02-17T20:35:00"
}
```

---

### 3. Get Payment by Task Assignment (NEW - Lookup)
**GET** `/payments/by-task/{taskAssignmentId}`

Gets the payment record for a specific task.

**Response:**
```json
{
  "id": 1,
  "postId": 10,
  "payerId": 1,
  "payeeId": 2,
  "amount": 50.00,
  "status": "SUCCEEDED",
  "stripePaymentIntentId": "pi_1234567890",
  "stripeClientSecret": "pi_1234567890_secret_abcdefg",
  "errorMessage": null,
  "createdAt": "2026-02-17T20:30:00",
  "paidAt": "2026-02-17T20:35:00"
}
```

---

## Reviews

### 1. Create Review
**POST** `/reviews`

Creates a review after a task is completed. User can review the other participant.

**Request Body:**
```json
{
  "taskAssignmentId": 5,
  "rating": 5,
  "comment": "Great helper! Very responsive."
}
```

**Response:**
```json
{
  "id": 1
}
```

**Error Codes:**
- `DUPLICATE_REVIEW`: User already reviewed this task
- `BAD_REQUEST`: Invalid rating (must be 1-5)

---

### 2. Get Review by Task Assignment (NEW - Lookup)
**GET** `/reviews/by-task/{taskAssignmentId}`

Gets the review for a specific task.

**Response:**
```json
{
  "id": 1,
  "taskAssignmentId": 5,
  "reviewerId": 1,
  "revieweeId": 2,
  "rating": 5,
  "comment": "Great helper! Very responsive.",
  "createdAt": "2026-02-17T20:40:00"
}
```

---

## Error Handling

### Error Response Format
All errors now return a structured error response with error codes:

```json
{
  "errorCode": "DUPLICATE_REVIEW",
  "message": "You already reviewed this task",
  "details": null
}
```

### Common Error Codes

| Code | HTTP Status | Description |
|------|------------|-------------|
| `BAD_REQUEST` | 400 | Invalid input data |
| `DUPLICATE_REVIEW` | 400 | User already reviewed this task |
| `NOT_FOUND` | 404 | Resource not found |
| `UNAUTHORIZED` | 401 | Not authorized to perform action |
| `PAYMENT_NOT_FOUND` | 404 | Payment record doesn't exist |
| `TASK_NOT_FOUND` | 404 | Task assignment doesn't exist |

---

## Frontend Integration Checklist

### Setup
- [ ] Configure API base URL: `http://localhost:8080/api`
- [ ] Store JWT token from `/auth/login` response
- [ ] Add token to all request headers

### Conversations Flow
- [ ] Use `POST /conversations/get-or-create` to open/create DMs
- [ ] Use `GET /conversations` to show inbox list
- [ ] Use `GET /conversations/{id}/messages` to load chat history
- [ ] Use `POST /conversations/messages` to send messages

### Payments Flow
- [ ] Create payment: `POST /payments` → get `stripeClientSecret`
- [ ] Confirm with Stripe.js: `stripe.confirmCardPayment(clientSecret)`
- [ ] Get payment status: `GET /payments/by-task/{taskId}`

### Reviews Flow
- [ ] After task completion: `POST /reviews` to create review
- [ ] Handle error code `DUPLICATE_REVIEW` gracefully
- [ ] Fetch review: `GET /reviews/by-task/{taskId}`

---

## Testing with cURL

### Create/Get Conversation
```bash
# Get token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"karim","password":"KarimKhalil"}' \
  | jq -r '.token')

# Get or create conversation
curl -X POST http://localhost:8080/api/conversations/get-or-create \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"type":"DIRECT","participantIds":[2]}'
```

### List Conversations
```bash
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/conversations
```

### Create Payment
```bash
curl -X POST http://localhost:8080/api/payments \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "postId": 10,
    "payeeId": 2,
    "amount": 50.00,
    "stripePaymentIntentId": null
  }'
```

### Create Review
```bash
curl -X POST http://localhost:8080/api/reviews \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "taskAssignmentId": 5,
    "rating": 5,
    "comment": "Great work!"
  }'
```

---

## Summary of New/Changed Endpoints

### ✅ NEW Endpoints
- `POST /conversations/get-or-create` - Idempotent conversation creation
- `GET /conversations` - List all conversations with last message
- `GET /payments/by-task/{taskAssignmentId}` - Lookup payment by task
- `GET /reviews/by-task/{taskAssignmentId}` - Lookup review by task

### ✅ IMPROVED
- All errors now return structured `{errorCode, message, details}` format
- Conversation listing includes last message and participant info
- Consistent error codes across endpoints

### ✅ PRESERVED
- All existing endpoints work as before
- Backward compatible changes
- No breaking changes to response formats

---

## Support & Debugging

**For CORS issues:**
- Ensure backend CORS config allows `http://localhost:3000`
- Check that Authorization headers are allowed

**For 404 errors:**
- Verify user/task/conversation exists in database
- Check user authorization (friend relationships, etc.)

**For token issues:**
- Ensure token is passed in header: `Authorization: Bearer <token>`
- Tokens expire after 1 hour (JWT expiration)
- Login again to get a new token

---

**Last Updated:** February 28, 2026
**API Version:** 1.2.0 (Extended)

