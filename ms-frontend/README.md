ms-frontend
===========

Minimal Vite+React admin UI for Products, Users, Orders and a metrics home page.


Quick start (local development):

1. cd ms-frontend
2. npm install
3. VITE_API_URL=http://localhost:8080 npm run dev

Build and run with Docker:

1. docker compose -f ../docker-compose.frontend.yml up --build
2. Open http://localhost:5000

Notes:
- The frontend reads API base from `VITE_API_URL` (default: http://host.docker.internal:8080).
- Docker and quick dev use host port `5000`.
 - When run in Docker, the frontend is built in production mode and the container includes an Nginx proxy so the UI calls relative `/api/v1/...` paths that are proxied to the services on the host (`host.docker.internal`).
 - Use `VITE_USE_PROXY=true` (set in `docker-compose.frontend.yml`) to enable proxy mode in the container.
- The UI assumes the backend exposes REST endpoints: `/products`, `/users`, `/orders`, and `/metrics` for the homepage.
