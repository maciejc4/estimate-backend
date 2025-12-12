# Authentication Architecture

## Overview

Two authentication providers, selected via Spring Profile:

| Profile | Provider | Best For |
|---------|----------|----------|
| `jwt` (default) | Custom JWT | Development, testing |
| `gcp` | Firebase/GCP Identity Platform | Production on GCP |

## Switching Providers

```bash
# Environment variable
export SPRING_PROFILES_ACTIVE=jwt  # or gcp

# Maven
./mvnw spring-boot:run -Dspring-boot.run.profiles=gcp

# Docker
docker run -e SPRING_PROFILES_ACTIVE=gcp estimate-backend
```

## Architecture

```
┌─────────────────────────────────────────────┐
│           Application Layer                  │
│  LoginUserService / RegisterUserService      │
└─────────────────┬───────────────────────────┘
                  │
┌─────────────────▼───────────────────────────┐
│         AuthenticationProviderPort           │
│              (Domain Port)                   │
└─────────────────┬───────────────────────────┘
                  │
    ┌─────────────┴─────────────┐
    ▼                           ▼
┌───────────────┐       ┌───────────────┐
│ CustomJwt     │       │ GcpIdentity   │
│ Provider      │       │ Provider      │
│ @Profile(jwt) │       │ @Profile(gcp) │
└───────────────┘       └───────────────┘
```

## JWT Profile Features

- Password hashing with bcrypt
- Rate limiting: 5 failed attempts
- Account lockout: 15 minutes
- Token expiration: 24 hours

## GCP Profile Features

- Firebase Authentication
- MFA support
- OAuth providers (Google, Facebook)
- User data synced to MongoDB

## GCP Profile Setup

1. Create Firebase project in GCP Console
2. Enable Email/Password authentication
3. Set environment variables:

```bash
export SPRING_PROFILES_ACTIVE=gcp
export GCP_PROJECT_ID=your-project-id
export GOOGLE_APPLICATION_CREDENTIALS=/path/to/service-account.json
```

## Migration

**JWT → GCP:** Users must reset passwords (bcrypt hashes cannot migrate to Firebase)

**GCP → JWT:** Export users from Firebase, import to MongoDB with temporary passwords

## Configuration Files

- `application.properties` - Common settings, defaults to jwt profile
- `application-jwt.yml` - JWT-specific settings
- `application-gcp.yml` - GCP-specific settings

## Security

Both profiles:
- Bearer token authentication
- CORS configuration
- CSRF disabled (stateless API)
- Role-based access (USER, ADMIN)
