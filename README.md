# Kata Bookstore (full stack)

Online bookstore kata: **React** UI + **Spring Boot** REST API, per **`requirements/project_description.txt`** (there is no `requirements.txt` in this repo—use that file as the spec).

- **Backend:** `kata_backend/` (this README’s **Kata Backend** section).
- **Frontend:** to be added in a separate folder (kata requirement).

---

## Kata Backend

Spring Boot **4.x**, **Java 17**, **H2** (dev), **session-based auth**, books, cart, orders, **Swagger UI**.

### Prerequisites

| Requirement | Notes |
|-------------|--------|
| **JDK 17** | `java -version` should report 17+. |
| **Network** (first build) | Maven Wrapper may download Maven and dependencies. |

You do **not** need a global Maven install if you use **`mvnw`** / **`mvnw.cmd`** in `kata_backend/`.

### Install (first time / after clone)

1. Open a terminal at the repo root (or go straight to `kata_backend/`).
2. From **`kata_backend/`**, run a one-time dependency download and compile:

   **Windows (PowerShell or cmd):**

   ```bat
   cd kata_backend
   mvnw.cmd -q dependency:go-offline compile
   ```

   **macOS / Linux:**

   ```bash
   cd kata_backend
   ./mvnw -q dependency:go-offline compile
   ```

   This downloads all Maven dependencies (including Spring Boot, H2, springdoc, etc.) into your local `~/.m2` cache.

### Run the application

Default profile is **`dev`** (see `kata_backend/src/main/resources/application.properties` → `spring.profiles.active`).

**Windows:**

```bat
cd kata_backend
mvnw.cmd spring-boot:run
```

**macOS / Linux:**

```bash
cd kata_backend
./mvnw spring-boot:run
```

- **API base URL:** http://localhost:8080 (unless you set `server.port`).
- **H2 console (dev profile):** http://localhost:8080/h2-console — JDBC URL from `application-dev.properties` (typically in-memory `jdbc:h2:mem:bookstore`), user **`sa`**, empty password unless you changed it.

To use another profile (e.g. production-style config):

```bat
set SPRING_PROFILES_ACTIVE=prod
mvnw.cmd spring-boot:run
```

*(Unix: `export SPRING_PROFILES_ACTIVE=prod`.)*

### Run tests

From **`kata_backend/`**:

```bat
mvnw.cmd test
```

```bash
./mvnw test
```

This runs the full unit / integration test suite (books, auth, cart, orders, etc.), aligned with the kata’s **TDD** guidance.

### API documentation (Swagger UI)

With the backend running:

| Resource | URL |
|----------|-----|
| **Swagger UI** | http://localhost:8080/swagger-ui.html |
| **OpenAPI JSON** | http://localhost:8080/v3/api-docs |

**Cart** and **orders** expect a **logged-in session** (`JSESSIONID` cookie after **`POST /auth/login`**). For reliable cookie behavior, use **Postman** or a **browser**; Swagger “Try it out” may not always retain the session the same way.

### API overview (quick reference)

| Area | Method | Path | Notes |
|------|--------|------|--------|
| Books | GET | `/books/all` | Public |
| Register | POST | `/auth/register` | JSON body |
| Login | POST | `/auth/login` | Sets session cookie |
| Logout | POST | `/auth/logout` | Clears session |
| Cart | GET | `/cart` | Requires session |
| Cart | POST | `/cart/items` | JSON: `bookId`, `quantity` |
| Cart | PATCH | `/cart/items/{bookId}` | JSON: `quantity` |
| Cart | DELETE | `/cart/items/{bookId}` | |
| Orders | POST | `/orders` | Checkout (empty body); requires session + non-empty cart |
| Orders | GET | `/orders` | List current user’s orders |

Use **`Content-Type: application/json`** on requests with a body.

### CORS (for the future React app)

Dev CORS allows **`http://localhost:5173`** with credentials so the Vite dev server can call the API with cookies when the front-end is added.

---

## Kata Frontend (planned)

As Per requirements React app in a **separate folder**, list books, cart, checkout UI, validation and error handling. Steps will be documented here once that app exists.
