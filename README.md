# Estimate Backend

Backend API for construction cost estimation system.

## Technology Stack

- Java 21
- Spring Boot 3.4
- MongoDB (embedded for development)
- JWT Authentication
- OpenPDF for PDF generation

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
- `POST /api/auth/demo` - Demo login (no registration required)

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
- `GET /api/estimates/{id}/pdf?detail=full|basic` - Generate PDF

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
