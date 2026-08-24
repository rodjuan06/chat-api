# chatAPI

A REST API for managing chats and messages, built with Spring Boot, MongoDB, PostgreSQL, and Spring Security.

## Tech stack

- Java 25
- Spring Boot 4.1.0
- Spring Web
- Spring Data MongoDB
- Spring Data JPA with PostgreSQL
- Spring Security
- JSON Web Token dependencies
- Maven

## Requirements

- Java 25
- Maven (or the included Maven Wrapper)
- MongoDB
- PostgreSQL

## Configuration

Copy the example environment file:

```bash
cp .env.example .env
```

Set values appropriate for your environment:

```dotenv
MONGO_CONNECTION_STRING=mongodb://localhost:27017/chat_db
AUTO_INDEX_CREATION=true
POSTGRES_USER=chat_api
POSTGRES_PASSWORD=admin123456
JWT_SECRET=replace_with_a_secure_secret
```

The default profile is `dev`. Production configuration is available in `application-prod.yml` and requires `MONGO_CONNECTION_STRING`, `POSTGRES_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`, and `JWT_SECRET`.

## Running

```bash
./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`. Use API paths without a trailing slash, for example:

```text
http://localhost:8080/api/v1/chats
```

## API endpoints

### Chats

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/chats` | List all chats |
| `GET` | `/api/v1/chats/{id}` | Get a chat by ID |
| `POST` | `/api/v1/chats` | Create a chat |
| `PUT` | `/api/v1/chats/{id}` | Update a chat |
| `DELETE` | `/api/v1/chats/{id}` | Delete a chat |

Example chat payload:

```json
{
  "name": "General",
  "members": 3
}
```

### Messages

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/api/v1/chats/{chatId}/messages` | List messages in a chat |
| `POST` | `/api/v1/chats/{chatId}/messages` | Create a message |
| `PUT` | `/api/v1/chats/{chatId}/messages/{id}` | Update a message |
| `DELETE` | `/api/v1/chats/{chatId}/messages/{id}` | Delete a message |

Example message payload:

```json
{
  "sender": "user@example.com",
  "text": "Hello"
}
```

## Authentication status

Security is configured as stateless and protects all endpoints except `/api/v1/auth/**`. JWT-related dependencies and configuration are present, but authentication and token-generation endpoints are still under development.

## Testing

```bash
./mvnw test
```

Tests require the configured database services to be available.
