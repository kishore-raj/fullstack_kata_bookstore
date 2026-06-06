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
| `src/App.jsx` | Router, layout, nav, cart total in header, `Outlet` context (`refreshCart`). |
| `src/pages/*.jsx` | One screen per file: books, cart, login, checkout, orders. |
| `src/App.css`, `src/index.css` | Minimal layout and global styles. |

## Assessment notes

- **TDD:** The kata asks to *take TDD into consideration*; automated **frontend** tests are not wired here—focus is a small, readable UI. Prefer tests on the backend or add e.g. Vitest + RTL later if you extend the kata.
- **HTTP status codes:** Handled on the server; the client branches on `401` for auth and shows API error messages for other failures where relevant.

## Related

- Backend: `kata_backend/` (or sibling folder per repo layout).
- Full kata text: `requirements/project_description.txt` (repo root).
