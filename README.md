# FindMyPG

FindMyPG is an India-first PG and coliving marketplace built as an Angular single-page application and a Spring Boot REST API, backed by PostgreSQL. The product includes searchable stays, listing details, tenant and owner workspaces, visit and booking requests, messaging, notifications, reports, image uploads, and an admin listing review queue.

## Architecture

```text
Angular 22 client (frontend/) ── HttpOnly session cookie / JSON ── Spring Boot 3 API (backend/)
                                                                    │
                                                    Spring Data JPA + Flyway migrations
                                                                    │
                                                               PostgreSQL
```

| Area | Implementation |
| --- | --- |
| Client | Angular standalone components, Angular Router, TypeScript, responsive design tokens and accessible form controls |
| API | Spring Boot 3.4, Spring Web, Bean Validation, Spring Security resource server |
| Database | PostgreSQL, JPA entities, versioned Flyway migrations |
| Authentication | BCrypt password hashes, signed HS256 JWTs in HttpOnly, SameSite=Strict cookies, role-based authorization |
| Listing photos | JPEG/PNG uploads, 8 MB limit, decoded and re-encoded images, UUID filenames; local disk for development |
| Payments | Not enabled. Booking requests never claim payment or reservation success. |

## Project structure

```text
backend/
  pom.xml
  src/main/java/in/findmypg/api/     REST controllers, entities, repositories, security
  src/main/resources/application.yml
  src/main/resources/db/migration/   PostgreSQL schema migrations
frontend/
  angular.json
  src/app/core/                      API client, authentication state, domain models
  src/app/pages/                     Home, search, stay, auth and workspaces
  src/styles.scss                    Marketplace design system
legacy-next/                         Previous Next.js implementation, retained for reference
docker-compose.yml                   Local PostgreSQL service
```

## Data model

The schema includes accounts (tenant, owner, admin), listings, listing amenities, favorites, booking requests, visit requests, reviews, listing reports, conversations, messages, notifications, and admin audit events. Public listing DTOs omit exact street addresses and private contact information. Demo seed listings have `demo=true`, display a Demo badge, and are not verified. No demo reviews are seeded.

Flyway applies schema changes at API startup. Hibernate uses `ddl-auto: validate`; migrations are the schema source of truth.

## Run locally (Windows PowerShell)

Requirements: Docker Desktop, Java 17+, Maven 3.9+, Node.js 22.22.3+ or 24.15.0+ and npm.

1. Start PostgreSQL:

   ```powershell
   docker compose up -d db
   ```

2. Configure the local API environment and start Spring Boot:

   ```powershell
   $env:DATABASE_URL = "jdbc:postgresql://localhost:5432/findmypg"
   $env:DATABASE_USER = "findmypg"
   $env:DATABASE_PASSWORD = "findmypg"
   $env:JWT_SECRET = "replace-this-local-value-with-a-random-secret-of-at-least-32-characters"
   $env:CORS_ORIGINS = "http://localhost:4200"
   $env:COOKIE_SECURE = "false"
   $env:DEMO_SEED = "true"
   mvn -f backend/pom.xml spring-boot:run
   ```

3. Install and start the Angular app in a second terminal:

   ```powershell
   cd frontend
   npm install
   npm start
   ```

   Visit [http://localhost:4200](http://localhost:4200). On localhost the client sends API calls to `http://localhost:8080`; other deployments should reverse proxy `/api` and `/uploads` to the API or configure the client API base URL for that deployment.

The defaults are for local development only. Do not use sample database credentials or the default JWT secret outside a local machine. Register tenant or owner accounts in the app. Never create an admin through public signup; provision the role through a protected operator process.

## Environment configuration

See [.env.example](.env.example) for server variables. Spring Boot reads environment variables directly. For deployment provide a strong random `JWT_SECRET`, `DATABASE_URL`, `DATABASE_USER`, `DATABASE_PASSWORD`, exact `CORS_ORIGINS`, `COOKIE_SECURE=true`, `PUBLIC_API_URL`, and a persistent `UPLOAD_DIR`. Set `DEMO_SEED=false` when sample inventory is not wanted. Store production secrets in your deployment platform’s secret manager.

The local upload directory is not shared storage. Before running multiple API instances, replace local disk storage with private object storage and configure safe image delivery. The image endpoint only accepts JPEG/PNG, checks size and decoded dimensions, and re-encodes images to strip metadata. Add malware scanning and storage lifecycle policies for production.

## Booking and payments

The app creates requests, not instant confirmations. Owners accept or decline a request; an accepted stay can be marked completed. Rent and deposit appear before submission. Payment collection is disabled until a provider’s test credentials, webhook signature verification, idempotency, refunds, and reconciliation are configured. No payment success is simulated.

## Operations and security notes

- Browser sessions use an HttpOnly, SameSite=Strict signed cookie. Production must set `COOKIE_SECURE=true` and serve over HTTPS.
- Roles are enforced server-side. Admin routes are not accessible through public registration. Verification is an explicit admin action.
- Login and registration have a basic per-process IP rate limit. Use a shared gateway rate limiter for multi-instance deployments.
- API inputs are validated. Angular interpolation escapes text; uploaded images are decoded and re-encoded. Put request-size limits and security headers at the reverse proxy.
- Admin listing verification and report decisions are written to `audit_events`. Support addresses in the UI are examples; configure monitored mailboxes before launch.
- Legal pages are product drafts and need Indian legal review. The demo does not verify identity, guarantee property condition, or validate owner claims.
- Configure backups, restore drills, monitoring, structured logs, alerting, TLS, CSP/security headers, data retention/deletion processes, and privacy notices before production launch.

## Build and checks

```powershell
mvn -f backend/pom.xml -DskipTests package
cd frontend
npm run build
```

Migrations are in `backend/src/main/resources/db/migration`. Automated tests and an end-to-end browser/database smoke test should be added before public launch.
