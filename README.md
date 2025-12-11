# Estimate Backend

Backend API for construction cost estimation system using reactive programming with Spring WebFlux.

## Technology Stack

- Java 21 with Virtual Threads (Project Loom)
- Spring Boot 3.4 with WebFlux (Reactive)
- MongoDB Reactive (embedded for development)
- JWT Authentication
- Clean Architecture with Domain-Driven Design

## Prerequisites

- Java 21 JDK
- Maven 3.8+
- Docker (optional, for containerized deployment)

## Getting Started

### Running Locally

```bash
# Clone the repository
git clone https://github.com/maciejc4/estimate-backend.git
cd estimate-backend

# Run the application
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

### Running Tests

```bash
./mvnw clean test
```

### Building

```bash
./mvnw clean package
```

### Docker

```bash
# Build Docker image
docker build -t estimate-backend .

# Run container
docker run -p 8080:8080 estimate-backend
```

## API Endpoints

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login

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

| Variable | Description | Default |
|----------|-------------|---------|
| `JWT_SECRET` | Secret key for JWT tokens | (dev default) |
| `SPRING_DATA_MONGODB_URI` | MongoDB connection URI | embedded |

## Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Deployment to GCP Cloud Run

```bash
# Build and push to Container Registry
gcloud builds submit --tag gcr.io/PROJECT_ID/estimate-backend

# Deploy to Cloud Run
gcloud run deploy estimate-backend \
  --image gcr.io/PROJECT_ID/estimate-backend \
  --platform managed \
  --region europe-central2 \
  --allow-unauthenticated
```

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
- Passwords stored securely using bcrypt

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

- Passwords hashed with bcrypt
- Protection against XSS and CSRF attacks
- Rate limiting for login attempts (account lockout)
- Regular security audits
- Sensitive data encryption
- JWT tokens for authentication

### 5. Architecture

- Clean Architecture with Domain-Driven Design
- Reactive programming with Spring WebFlux
- Use cases extracted to domain layer
- Virtual Threads (Project Loom) enabled
- MongoDB for persistence

### 6. Notes

- PDF generation moved to frontend
- Demo mode removed (will be frontend-only feature)
- Focus on backend business logic and API endpoints

