# Estimate Backend

Backend API for construction cost estimation using Spring WebFlux.

## Quick Start

```bash
# Run (uses embedded MongoDB by default)
./mvnw spring-boot:run

# Test
./mvnw test

# Build
./mvnw clean package
```

App runs on `http://localhost:8080`

## Authentication Profiles

Switch between authentication providers:

```bash
# JWT (default)
./mvnw spring-boot:run

# GCP Identity Platform
SPRING_PROFILES_ACTIVE=gcp GCP_PROJECT_ID=your-project ./mvnw spring-boot:run
```

See [docs/authentication.md](docs/authentication.md) for details.

## API Overview

| Endpoint | Description |
|----------|-------------|
| `POST /api/auth/register` | Register user |
| `POST /api/auth/login` | Login (JWT only) |
| `GET /api/users/me` | Current user |
| `GET/POST/PUT/DELETE /api/works` | Manage works |
| `GET/POST/PUT/DELETE /api/templates` | Manage templates |
| `GET/POST/PUT/DELETE /api/estimates` | Manage estimates |
| `GET /api/admin/*` | Admin operations |

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Auth profile: `jwt` or `gcp` | `jwt` |
| `JWT_SECRET` | JWT signing key | dev default |
| `GCP_PROJECT_ID` | GCP project (gcp profile) | - |

## Docker

```bash
docker build -t estimate-backend .
docker run -p 8080:8080 estimate-backend
```

## Deploy to Cloud Run

```bash
gcloud builds submit --tag gcr.io/PROJECT_ID/estimate-backend
gcloud run deploy estimate-backend \
  --image gcr.io/PROJECT_ID/estimate-backend \
  --set-env-vars="SPRING_PROFILES_ACTIVE=jwt"
```

## Documentation

- [docs/authentication.md](docs/authentication.md) - Authentication architecture
- [docs/architecture.md](docs/architecture.md) - System architecture
- [docs/api.md](docs/api.md) - API reference
- [docs/DESIRED_ARCHITECTURE.md](docs/DESIRED_ARCHITECTURE.md) - Microservices plan
- [CLAUDE.md](CLAUDE.md) - Development guidelines

## Tech Stack

- Java 21 / Spring Boot 3.4 / WebFlux
- MongoDB (embedded for dev)
- JWT or Firebase Authentication
- Clean Architecture / DDD
