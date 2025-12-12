# Estimate Backend

Backend API for construction cost estimation system using reactive programming with Spring WebFlux.

## Technology Stack

- Java 21 with Virtual Threads (Project Loom)
- Spring Boot 3.4 with WebFlux (Reactive)
- MongoDB Reactive (embedded for development)
- **Multi-Provider Authentication** (JWT or GCP Identity Platform)
- Firebase Admin SDK (for GCP Identity Platform)
- Clean Architecture with Domain-Driven Design

## Authentication Providers

This application supports two authentication providers that can be selected at deployment time:

### 1. Custom JWT Authentication (Default)
- Self-managed authentication with JWT tokens
- Password management with bcrypt
- Rate limiting and account lockout
- Best for: Development, testing, self-hosted deployments

### 2. GCP Identity Platform (Firebase Authentication)
- Managed authentication service from Google Cloud
- Supports OAuth, MFA, and more
- User data synced to MongoDB for business logic
- Best for: Production deployments on GCP

**See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed authentication architecture and how to switch providers.**

## Prerequisites

- Java 21 JDK
- Maven 3.8+
- Docker (optional, for containerized deployment)
- GCP Project with Identity Platform enabled (for `gcp` profile)

## Getting Started

### Running Locally (JWT Authentication)

```bash
# Clone the repository
git clone https://github.com/maciejc4/estimate-backend.git
cd estimate-backend

# Run with JWT authentication (default)
./mvnw spring-boot:run
```

### Running with GCP Identity Platform

```bash
# Set environment variables
export SPRING_PROFILES_ACTIVE=gcp
export GCP_PROJECT_ID=your-project-id
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account.json

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## Switching Authentication Providers

### Method 1: Environment Variable

```bash
# JWT Authentication
export SPRING_PROFILES_ACTIVE=jwt
./mvnw spring-boot:run

# GCP Identity Platform
export SPRING_PROFILES_ACTIVE=gcp
export GCP_PROJECT_ID=your-project-id
./mvnw spring-boot:run
```

### Method 2: Application Properties

Edit `src/main/resources/application.properties`:

```properties
# For JWT
spring.profiles.active=jwt

# For GCP
spring.profiles.active=gcp
```

### Method 3: Maven Command

```bash
# JWT
./mvnw spring-boot:run -Dspring-boot.run.profiles=jwt

# GCP
./mvnw spring-boot:run -Dspring-boot.run.profiles=gcp
```

### Method 4: Docker

```bash
# JWT Authentication
docker run -e SPRING_PROFILES_ACTIVE=jwt \
           -e JWT_SECRET=your-secret \
           -p 8080:8080 estimate-backend

# GCP Identity Platform
docker run -e SPRING_PROFILES_ACTIVE=gcp \
           -e GCP_PROJECT_ID=your-project \
           -e GOOGLE_APPLICATION_CREDENTIALS=/creds.json \
           -v /path/to/creds.json:/creds.json:ro \
           -p 8080:8080 estimate-backend
```

## Running Tests

```bash
# All tests with default (JWT) profile
./mvnw clean test

# Tests with specific profile
./mvnw test -Dspring.profiles.active=jwt
```

## Building

```bash
./mvnw clean package
```

## Docker

```bash
# Build Docker image
docker build -t estimate-backend .

# Run container (JWT by default)
docker run -p 8080:8080 estimate-backend

# Run with GCP Identity Platform
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=gcp \
  -e GCP_PROJECT_ID=your-project-id \
  estimate-backend
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login (JWT profile only)

### Users
- `GET /api/users/me` - Get current user
- `PUT /api/users/me` - Update profile
- `DELETE /api/users/me` - Delete account

### Works (Prace)
- `GET /api/works` - List works
- `GET /api/works/{id}` - Get work
- `POST /api/works` - Create work
- `PUT /api/works/{id}` - Update work
- `DELETE /api/works/{id}` - Delete work

### Templates (Szablony remontowe)
- `GET /api/templates` - List templates
- `GET /api/templates/{id}` - Get template
- `POST /api/templates` - Create template
- `PUT /api/templates/{id}` - Update template
- `DELETE /api/templates/{id}` - Delete template

### Estimates (Kosztorysy)
- `GET /api/estimates` - List estimates
- `GET /api/estimates/{id}` - Get estimate
- `POST /api/estimates` - Create estimate
- `PUT /api/estimates/{id}` - Update estimate
- `DELETE /api/estimates/{id}` - Delete estimate

### Admin (requires ADMIN role)
- `GET /api/admin/users` - List all users
- `DELETE /api/admin/users/{id}` - Delete user
- `GET /api/admin/works` - List all works
- `GET /api/admin/templates` - List all templates
- `GET /api/admin/estimates` - List all estimates

## Environment Variables

### Common Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SPRING_PROFILES_ACTIVE` | Active authentication profile: `jwt` or `gcp` | `jwt` |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection URI | embedded |

### JWT Profile Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Secret key for JWT tokens | (dev default - change in production!) |
| `JWT_EXPIRATION_MS` | Token expiration in milliseconds | `86400000` (24h) |

### GCP Profile Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `GCP_PROJECT_ID` | Google Cloud Project ID | Yes |
| `GOOGLE_APPLICATION_CREDENTIALS` | Path to service account JSON file | No (uses default credentials if not set) |

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Deployment to GCP Cloud Run

### With JWT Authentication

```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/estimate-backend

# Deploy to Cloud Run with JWT
gcloud run deploy estimate-backend \
  --image gcr.io/PROJECT_ID/estimate-backend \
  --platform managed \
  --region europe-central2 \
  --set-env-vars="SPRING_PROFILES_ACTIVE=jwt" \
  --set-env-vars="JWT_SECRET=$(openssl rand -base64 32)" \
  --allow-unauthenticated
```

### With GCP Identity Platform

```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/estimate-backend

# Deploy to Cloud Run with GCP Identity Platform
gcloud run deploy estimate-backend \
  --image gcr.io/PROJECT_ID/estimate-backend \
  --platform managed \
  --region europe-central2 \
  --set-env-vars="SPRING_PROFILES_ACTIVE=gcp" \
  --set-env-vars="GCP_PROJECT_ID=PROJECT_ID" \
  --allow-unauthenticated
```

**Note:** When using GCP Identity Platform on Cloud Run, service account credentials are automatically available through Application Default Credentials (ADC).

## Project Structure

```
src/
├── main/
│   ├── java/com/estimate/
│   │   ├── adapter/           # Adapters (Web Controllers, Persistence)
│   │   ├── application/        # Use Cases (Application Services)
│   │   ├── domain/            # Domain Models and Ports
│   │   │   ├── model/         # Domain Entities and Value Objects
│   │   │   └── port/          # Ports (Interfaces)
│   │   │       ├── in/        # Input Ports (Use Cases)
│   │   │       └── out/       # Output Ports (Repositories, etc.)
│   │   └── infrastructure/    # Infrastructure (Security, Config)
│   │       ├── auth/          # Authentication Providers
│   │       │   ├── jwt/       # JWT Authentication [@Profile("jwt")]
│   │       │   └── gcp/       # GCP Identity Platform [@Profile("gcp")]
│   │       └── security/      # Security Configuration
│   └── resources/
│       ├── application.properties           # Common configuration
│       ├── application-jwt.yml              # JWT profile configuration
│       └── application-gcp.yml              # GCP profile configuration
└── test/                      # Tests
```

## Documentation

- **[ARCHITECTURE.md](ARCHITECTURE.md)** - Detailed authentication architecture, sequence diagrams, migration guide
- **[CLAUDE.md](CLAUDE.md)** - Development guidelines and best practices
- **[README.md](README.md)** - This file

## Requirements

### 1. Application Overview

- Web application for small construction companies
- Goal: Quick generation of cost estimates for clients
- Reactive architecture using Spring WebFlux for better scalability

### 2. User Scenarios

#### 2.1 Account Management
- User creates account and logs in
- User can delete their account
- User can change password
- Passwords stored securely using bcrypt (JWT profile) or managed by Firebase (GCP profile)

#### 2.2 Works (Construction Activities)
- User creates works (e.g., painting, priming, installing baseboards)
- Each work has a unit (e.g., m² for painting, mb for baseboards)
- Materials can be added to each work with their consumption rate
- Users can view, edit, add, and delete their works

#### 2.3 Renovation Templates
- User defines renovation templates (e.g., "Bathroom Renovation")
- Templates composed of previously defined works
- New works can be added directly from template creation view
- Users can view, edit, add, and delete their templates

#### 2.4 Cost Estimates
- Create estimates for clients using renovation templates
- One estimate can contain multiple templates
- Estimates include: investor data, address, material cost, labor cost
- Optional: notes, validity date, start date
- Discounts can be applied separately to materials and labor
- Users can view, edit, add, and delete their estimates

#### 2.5 Company Data
- Users can define: company name, email, phone number
- This data is used in generated estimates

#### 2.6 Administrator Role
- Special administrator user can:
  - View estimates, works, and templates from all users
  - View registered users
  - Add and delete users

### 3. Code Quality Requirements

- Clean Code principles (naming, class/method length)
- SOLID principles
- Test coverage: unit, component, and integration tests
- All naming in English
- Trunk-based development
- Small commits (max 255 characters)

### 4. Security

#### JWT Profile
- Passwords hashed with bcrypt
- Rate limiting: 5 failed login attempts
- Account lockout: 15 minutes
- JWT token expiration: 24 hours

#### GCP Profile
- Password management by Firebase
- MFA support through Firebase
- OAuth providers (Google, Facebook, etc.)
- All Firebase security features

**Both Profiles:**
- Protection against XSS and CSRF attacks
- Secure token transmission (Bearer tokens)
- Role-based access control (USER, ADMIN)

### 5. Architecture

- Clean Architecture with Domain-Driven Design
- Hexagonal Architecture (Ports & Adapters)
- Reactive programming with Spring WebFlux
- Use cases extracted to domain layer
- Virtual Threads (Project Loom) enabled
- MongoDB for persistence
- **Strategy Pattern for authentication providers**

### 6. Notes

- PDF generation moved to frontend
- Demo mode removed (will be frontend-only feature)
- Focus on backend business logic and API endpoints
- Authentication provider can be switched without code changes (only configuration)

