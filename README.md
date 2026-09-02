# ChatAPI

REST API for chats and messages using polyglot persistence: PostgreSQL stores users and authentication data, while MongoDB stores chats and messages.

The API currently includes authentication and authorization, a reproducible Docker environment, health checks, OpenAPI documentation, DTO-based HTTP contracts and automated architecture verification.

## Tech stack

- Java 25
- Spring Boot 4.1
- Spring Web and Bean Validation
- Spring Data JPA, Hibernate and PostgreSQL
- Spring Data MongoDB
- Spring Security and JWT
- Spring Boot Actuator
- Spring Modulith
- Springdoc OpenAPI and Swagger UI
- Maven
- Docker and Docker Compose
- JUnit, Mockito and Testcontainers

## Architecture

The application is organized as a modular monolith. The `user` module owns registration, authentication and user persistence in PostgreSQL. The `chat` module owns chats and messages stored in MongoDB. Communication between modules happens through small public contracts instead of direct access to another module's repository.

HTTP endpoints use dedicated request and response DTOs. Clients do not control server-managed fields such as chat and message IDs, creation dates, `chatId` or `senderId`. Mappers translate between these DTOs and the MongoDB entities.

Spring Modulith verifies the module boundaries during the test suite with `ApplicationModules.verify()`.

## Requirements

For the complete containerized environment:

- Docker Engine
- Docker Compose

For running the application directly on the host:

- Java 25
- Maven, or the included Maven Wrapper
- PostgreSQL and MongoDB, installed locally or started through Docker Compose

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
JWT_EXPIRATION_MS=3600000
```

Generate a JWT secret with:

```bash
openssl rand -base64 32
```

Do not commit the `.env` file. The development profile connects to MongoDB on `localhost:27017` and PostgreSQL on `127.0.0.1:5432/chat_api`. The Docker profile uses the internal service names `mongo` and `postgres`. Production additionally requires `POSTGRES_URL` and reads all credentials from environment variables.

## Running with Docker Compose

Build the application image and start the API, PostgreSQL and MongoDB:

```bash
docker compose up --build
```

To run the stack in the background:

```bash
docker compose up -d --build
```

Check the status of the three containers:

```bash
docker compose ps
```

The expected state is `healthy` for `app`, `postgres` and `mongo`. The API is available at `http://localhost:8080`, and its health endpoint is available without authentication:

```bash
curl http://localhost:8080/actuator/health
```

Expected response:

```json
{"status":"UP"}
```

Follow all logs or only the application logs:

```bash
docker compose logs -f
docker compose logs -f app
```

Stop and remove the containers while preserving the database data:

```bash
docker compose down
```

PostgreSQL and MongoDB data is stored in the named volumes `postgres_data` and `mongo_data`. To also delete the database data and return to an empty environment, run:

```bash
docker compose down -v
```

The `-v` option permanently removes the local database volumes and should be used with care.

## Running the application from the IDE

To use breakpoints and run the Spring application from the IDE, start only the databases:

```bash
docker compose up -d postgres mongo
```

Then run `ChatApiApplication` with the `dev` profile, or use the Maven Wrapper:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

Do not run the `app` container and the application from the IDE at the same time, because both use port `8080`. If the complete stack is already running, stop only the containerized application first:

```bash
docker compose stop app
```

## Running locally

With PostgreSQL and MongoDB already running, start the application with:

```bash
SPRING_PROFILES_ACTIVE=dev ./mvnw spring-boot:run
```

The API is available at `http://localhost:8080`.

## OpenAPI and Swagger UI

With the application running, the interactive API documentation is available at:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

Registration and login are public. To test protected endpoints in Swagger UI:

1. Register a user through `POST /api/v1/auth/register`.
2. Log in through `POST /api/v1/auth/login` and copy the returned token.
3. Select **Authorize** in Swagger UI.
4. Enter only the JWT value. Swagger adds the `Bearer` prefix automatically.

The documentation includes the Bearer JWT security scheme, request and response DTOs, validation rules, examples and expected HTTP status codes.

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

Example response:

```json
{
  "id": "66d7703b701a5c46272c2210",
  "name": "General",
  "memberIds": [1, 2],
  "createdAt": "2026-09-02T17:30:00"
}
```

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
  "text": "Hello",
  "image": null
}
```

The optional `image` field is represented as a Base64 string when used. The server derives `senderId`, `chatId`, identifiers and dates; clients cannot choose these values. A chat member trying to modify another user's message receives `403 Forbidden`.

Example response:

```json
{
  "id": "66d7703b701a5c46272c2211",
  "chatId": "66d7703b701a5c46272c2210",
  "senderId": 1,
  "text": "Hello",
  "image": null,
  "date": "2026-09-02T17:31:00"
}
```

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

The suite currently has 42 tests. Unit and MVC slice tests use Mockito to cover services, controllers, validation and HTTP security responses. The `contextLoads` integration test starts temporary PostgreSQL and MongoDB instances with Testcontainers, so Docker must be running; no locally installed databases are required. A Spring Modulith test also verifies that module boundaries are respected.

## Current status and next steps

Completed milestones:

- Chat and message operations
- User registration, JWT authentication and authorization rules
- Dockerfile, Docker Compose and container health checks
- OpenAPI and Swagger UI documentation
- DTOs and mappers for user, chat and message HTTP contracts
- Testcontainers integration and automated module verification

Planned improvements include a standardized error response format, dedicated JWT filter tests, continuous integration, pagination, Flyway migrations, explicit member-management endpoints and deployment.
