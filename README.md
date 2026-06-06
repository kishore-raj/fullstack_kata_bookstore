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

# Kata Bookstore — Frontend (React)

React client for the **Online Bookstore** kata: it talks to the Spring Boot API over REST, uses **session cookies** (`credentials: 'include'`), and lives in a **separate folder** from the backend (see repo layout).

This README focuses on **how to run the frontend** and how it maps to the **assessment requirements**.

## Requirements coverage (frontend)

| # | Requirement | How it is addressed |
|---|----------------|---------------------|
| 1 | React application for an online bookstore | Vite + React 18 app in `kata_frontend/`. |
| 2 | List books with **title, author, price** | **Books** (`/books`) loads `GET /books/all` and renders a table. |
| 3 | Shopping cart — **add books** | Same page: quantity per row + **Add to cart** → `POST /cart/items`. User must be logged in (session). |
| 4 | Show cart, **change quantities**, **remove** items | **Cart** (`/cart`): `GET /cart`, **Update** → `PATCH /cart/items/{bookId}`, **Remove** → `DELETE /cart/items/{bookId}`. |
| 5 | **Checkout** with **order summary** | **Checkout** (`/checkout`): `POST /orders/checkout`, then shows order id, time, line items, and total. |

**Authentication (backend requirement, used by the UI):** **Login** (`/login`) supports **register** + **login** (`POST /auth/register`, `POST /auth/login`). **Logout** calls `POST /auth/logout`. The header shows the stored username and **Logout** when the session is valid (cart request succeeds), otherwise **Login** only.

**Orders list (extra):** **Orders** (`/orders`) calls `GET /orders/list` to preview past orders.

**Errors:** Failed API calls surface messages from the server where practical (e.g. empty cart on checkout, 401 → prompt to log in). Validation messages from the API (`fieldErrors`) are supported in the shared `client.js` layer.

## Prerequisites

- Node.js (LTS recommended) and npm.
- Backend running and reachable (start the Spring Boot app from `kata_backend/` per your setup).
- CORS and **credentials** configured on the API for this dev origin (e.g. `http://localhost:5173`) so the browser can send the session cookie.

## Run locally

1. **Start the Spring Boot API** (default base URL used below: `http://localhost:8080`).

2. **Configure the API base URL** — in this folder create `.env.development`:

   ```bash
   VITE_API_BASE_URL=http://localhost:8080
   ```

3. **Install dependencies** and start the dev server:

   ```bash
   npm install
   npm run dev
   ```

4. Open the URL Vite prints (usually **http://localhost:5173**).

## Other scripts

```bash
npm run build   # production build to dist/
npm run preview # serve dist/ locally
npm run lint    # ESLint
```

## Project structure (keep it simple)

| Path | Role |
|------|------|
| `src/api/client.js` | `fetch` wrapper: JSON, `credentials: 'include'`, throws `Error` with `status` / `fieldErrors`. |
| `src/api/paths.js` | API path constants. |
| `src/api/books.js`, `cart.js`, `auth.js`, `orders.js` | One small module per area; thin calls into `client` + `paths`. |
| `src/App.jsx` | Router, layout, cart total in header, `Outlet` context (`refreshCart`). |
| `src/pages/*.jsx` | One screen per file: books, cart, login, checkout, orders. |
| `src/App.css`, `src/index.css` | Minimal layout and global styles. |





