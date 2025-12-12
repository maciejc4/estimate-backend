# Estimate Backend - Development Guidelines

## Quick Reference

```bash
./mvnw spring-boot:run          # Run (embedded MongoDB)
./mvnw test                      # Test
./mvnw clean package             # Build
```

## Project Structure

```
src/main/java/com/estimate/
├── adapter/in/web/          # REST Controllers
├── adapter/out/             # Repository implementations
├── application/usecase/     # Application Services
├── domain/model/            # Entities, Value Objects
├── domain/port/in/          # Input Ports (Use Cases)
├── domain/port/out/         # Output Ports (Repositories)
└── infrastructure/          # Security, Config
    └── auth/jwt|gcp/        # Authentication providers
```

## Architecture Rules

1. **Dependencies point inward** - Domain has no external dependencies
2. **Domain is pure** - No Spring annotations in domain layer
3. **Ports define contracts** - Adapters implement them
4. **Use cases orchestrate** - Business logic stays in domain

## Authentication Profiles

```bash
# JWT (default)
./mvnw spring-boot:run

# GCP Identity Platform
SPRING_PROFILES_ACTIVE=gcp GCP_PROJECT_ID=xxx ./mvnw spring-boot:run
```

Profile-specific components use `@Profile("jwt")` or `@Profile("gcp")`.

## Code Standards

### Reactive
```java
// Good
return userRepository.findById(id)
    .flatMap(user -> calculateScore(user))
    .map(this::toDto);

// Bad - blocks reactive chain
User user = userRepository.findById(id).block();
```

### Clean Architecture
```java
// Good - Use case depends on port
public class LoginUserService {
    private final AuthenticationProviderPort authProvider;
}

// Bad - Use case depends on infrastructure
public class LoginUserService {
    private final JwtTokenProvider jwtProvider;
}
```

## Git Workflow

1. Work on master (trunk-based)
2. Small commits (<255 chars)
3. Run tests before commit
4. Push frequently

```bash
./mvnw test
git add -A
git commit -m "Add user authentication"
git push
```

## Commit Checklist

- [ ] Tests pass
- [ ] No secrets in code
- [ ] Clean code principles followed
- [ ] Profile-specific code has @Profile

## Environment Variables

| Variable | Profile | Description |
|----------|---------|-------------|
| `SPRING_PROFILES_ACTIVE` | all | `jwt` or `gcp` |
| `JWT_SECRET` | jwt | Token signing key |
| `GCP_PROJECT_ID` | gcp | Firebase project |

## Documentation

- [docs/authentication.md](docs/authentication.md) - Auth architecture
- [docs/architecture.md](docs/architecture.md) - System design
- [docs/api.md](docs/api.md) - API reference
- [docs/DESIRED_ARCHITECTURE.md](docs/DESIRED_ARCHITECTURE.md) - Microservices plan
