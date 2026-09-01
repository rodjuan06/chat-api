# chatAPI

REST API for chats and messages using polyglot persistence: PostgreSQL stores users and authentication data, while MongoDB stores chats and messages.

## Tech stack

- Java 25
- Spring Boot 4.1
- Spring Web and Bean Validation
- Spring Data JPA, Hibernate and PostgreSQL
- Spring Data MongoDB
- Spring Security and JWT
- Spring Modulith
- Maven
- JUnit and Mockito

## Requirements

- Java 25
- MongoDB
- PostgreSQL
- Maven, or the included Maven Wrapper

## Configuration

Copy the example environment file:

```bash
cp .env.example .env
```

Configure the local values:

```dotenv
MONGO_CONNECTION_STRING=mongodb://localhost:27017/chat_db
AUTO_INDEX_CREATION=true
POSTGRES_USER=chat_api
POSTGRES_PASSWORD=change_me
JWT_SECRET=base64_encoded_secret
```

Generate a JWT secret with:

```bash
openssl rand -base64 32
```

The development profile connects to MongoDB on `localhost:27017` and PostgreSQL on `127.0.0.1:5432/chat_api`. Production additionally requires `POSTGRES_URL` and reads all credentials from environment variables.

## Running locally

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

## Authentication

### Register

```http
POST /api/v1/auth/register
Content-Type: application/json
```

```json
{
  "name": "Maria",
  "email": "maria@example.com",
  "password": "password123"
}
```

Successful registration returns `201 Created`. E-mail addresses are trimmed and normalized to lowercase before storage.

### Login

```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "email": "maria@example.com",
  "password": "password123"
}
```

Successful login returns:

```json
{
  "token": "jwt-token"
}
```

Send this token to every chat and message endpoint:

```http
Authorization: Bearer jwt-token
```

Missing or invalid authentication returns `401 Unauthorized`.

## Chat endpoints

| Method | Endpoint | Result |
| --- | --- | --- |
| `GET` | `/api/v1/chats` | Lists chats containing the authenticated user |
| `GET` | `/api/v1/chats/{id}` | Returns a chat when the authenticated user is a member |
| `POST` | `/api/v1/chats` | Creates a chat and returns `201 Created` |
| `PUT` | `/api/v1/chats/{id}` | Updates the name of a chat accessible to the user |
| `DELETE` | `/api/v1/chats/{id}` | Deletes the chat and its messages; returns `204 No Content` |

Example creation request:

```json
{
  "name": "General",
  "memberIds": [1, 2]
}
```

The authenticated creator is always added to `memberIds`. Repeated IDs are removed, and every member ID must exist in PostgreSQL.

## Message endpoints

| Method | Endpoint | Result |
| --- | --- | --- |
| `GET` | `/api/v1/chats/{chatId}/messages` | Lists messages when the user belongs to the chat |
| `POST` | `/api/v1/chats/{chatId}/messages` | Creates a message and returns `201 Created` |
| `PUT` | `/api/v1/chats/{chatId}/messages/{id}` | Updates a message owned by the authenticated user |
| `DELETE` | `/api/v1/chats/{chatId}/messages/{id}` | Deletes a message owned by the authenticated user; returns `204 No Content` |

Example message request:

```json
{
  "text": "Hello"
}
```

The server derives `senderId` from the JWT. Clients cannot choose the sender. A chat member trying to modify another user's message receives `403 Forbidden`.

## Error responses

Errors are returned as JSON. Examples include:

- `400 Bad Request` for validation errors, invalid IDs or unknown chat members
- `401 Unauthorized` for missing, invalid or incorrect credentials
- `403 Forbidden` when modifying another user's message
- `404 Not Found` when a chat or message is unavailable to the authenticated user
- `409 Conflict` when registering an existing e-mail

## Testing

Run the test suite with:

```bash
./mvnw test
```

Unit tests use Mockito and do not require databases. The current `contextLoads` integration test requires the configured PostgreSQL and MongoDB services; replacing that dependency with Testcontainers is still planned.
