# Estimate Backend - Development Guidelines

Backend API for construction cost estimation system using reactive programming with Spring WebFlux.

## Technology Stack

- Java 21 with Virtual Threads (Project Loom)
- Spring Boot 3.4 with WebFlux (Reactive)
- MongoDB Reactive (embedded for development)
- **Multi-Provider Authentication** (JWT or GCP Identity Platform)
- Firebase Admin SDK (GCP Identity Platform integration)
- Clean Architecture with Domain-Driven Design
- Hexagonal Architecture (Ports & Adapters)

## Architecture Overview

### Layered Architecture

```
Presentation Layer (Adapters/In)
    ↓
Application Layer (Use Cases)
    ↓
Domain Layer (Core Business Logic, Ports)
    ↓
Infrastructure Layer (Adapters/Out, External Services)
```

### Authentication Architecture

The application uses **Strategy Pattern** to support multiple authentication providers:

- **JWT Profile** (`@Profile("jwt")`) - Default, self-managed authentication
- **GCP Profile** (`@Profile("gcp")`) - Firebase Authentication integration

**Key abstraction:** `AuthenticationProviderPort` interface in domain layer
**Implementations:** 
- `CustomJwtAuthenticationProvider` (infrastructure/auth/jwt)
- `GcpIdentityAuthenticationProvider` (infrastructure/auth/gcp)

See [ARCHITECTURE.md](ARCHITECTURE.md) for detailed diagrams and flows.

## Project Structure

```
src/main/java/com/estimate/
├── adapter/
│   ├── in/web/              # REST Controllers
│   └── out/                 # Repository implementations, External service adapters
├── application/usecase/      # Application Services (Use Cases)
├── domain/
│   ├── model/               # Domain Entities and Value Objects
│   ├── port/
│   │   ├── in/              # Input Ports (Use Case interfaces)
│   │   └── out/             # Output Ports (Repository interfaces, Service interfaces)
│   └── exception/           # Domain exceptions
└── infrastructure/
    ├── auth/                # Authentication providers
    │   ├── jwt/             # JWT implementation [@Profile("jwt")]
    │   └── gcp/             # GCP Identity Platform [@Profile("gcp")]
    ├── security/            # Spring Security configuration
    └── persistence/         # MongoDB configuration

src/main/resources/
├── application.properties      # Common configuration
├── application-jwt.yml         # JWT profile specific
└── application-gcp.yml         # GCP profile specific
```

## Development Principles

### 1. Clean Architecture (Hexagonal)

**Port:** Interface defining contract
**Adapter:** Implementation of a port

**Rules:**
- Domain layer has NO dependencies on outer layers
- Dependencies point INWARD (Dependency Inversion Principle)
- Use Cases orchestrate domain logic
- Adapters translate between external world and domain

**Example:**
```java
// Domain Port (Output)
public interface AuthenticationProviderPort {
    Mono<AuthenticationResult> authenticate(String email, String password);
}

// Infrastructure Adapter (JWT)
@Component
@Profile("jwt")
public class CustomJwtAuthenticationProvider implements AuthenticationProviderPort {
    // Implementation using JWT
}

// Infrastructure Adapter (GCP)
@Component
@Profile("gcp")
public class GcpIdentityAuthenticationProvider implements AuthenticationProviderPort {
    // Implementation using Firebase
}
```

### 2. Domain-Driven Design (DDD)

**Entities:** Objects with identity (User, Work, Estimate)
**Value Objects:** Objects without identity (Email, Money, AuthenticationResult)
**Aggregates:** Cluster of domain objects (Estimate + TemplateItems)
**Use Cases:** Application services orchestrating domain logic

**Rules:**
- Business logic in domain layer
- Rich domain models (not anemic)
- Use domain language (ubiquitous language)
- Aggregate roots control access to aggregates

### 3. Reactive Programming

- Use `Mono<T>` for single result
- Use `Flux<T>` for multiple results
- Chain operations functionally
- Handle errors with `onErrorResume`, `onErrorMap`
- Test with `StepVerifier`

**Example:**
```java
@Override
public Mono<AuthResult> register(RegisterUserCommand command) {
    return authenticationProvider.registerUser(userData)
            .doOnNext(result -> log.info("User registered: {}", result.getEmail()))
            .map(this::toAuthResult)
            .onErrorMap(EmailAlreadyExistsException.class, ex -> 
                new DuplicateResourceException("Email already exists"));
}
```

### 4. SOLID Principles

- **S**ingle Responsibility: Each class has one reason to change
- **O**pen/Closed: Open for extension, closed for modification
- **L**iskov Substitution: Subtypes must be substitutable
- **I**nterface Segregation: Many specific interfaces > one general
- **D**ependency Inversion: Depend on abstractions, not concretions

### 5. Security Best Practices

**Never commit:**
- API keys, secrets, passwords
- Service account JSON files
- Private keys

**Use environment variables:**
```java
@Value("${app.jwt.secret}")
private String jwtSecret;
```

**Validate input:**
```java
@Valid @RequestBody RegisterRequest request
```

**Rate limiting (JWT profile):**
- 5 failed attempts → 15 minute lockout

## Development Workflow

### 1. Before Starting

```bash
# Ensure you're on master branch
git checkout master
git pull origin master

# Ensure tests pass
./mvnw clean test
```

### 2. Making Changes

**Follow trunk-based development:**
- Small, incremental commits
- Commit messages max 255 characters
- Each commit should compile and pass tests

```bash
# Make changes
# ... edit files ...

# Run tests
./mvnw test

# Check for errors
./mvnw clean compile

# Commit
git add -A
git commit -m "Add user authentication with rate limiting"

# Push frequently
git push origin master
```

### 3. Testing

**Test levels:**
- **Unit tests:** Test single class in isolation (mock dependencies)
- **Integration tests:** Test multiple components together
- **Component tests:** Test use case with real repositories

**Example unit test:**
```java
class CustomJwtAuthenticationProviderTest {
    @Test
    void shouldAuthenticateValidUser() {
        // Arrange
        when(userRepository.findByEmail("user@example.com"))
            .thenReturn(Mono.just(user));
        
        // Act
        Mono<AuthenticationResult> result = provider.authenticate("user@example.com", "password");
        
        // Assert
        StepVerifier.create(result)
            .expectNextMatches(auth -> auth.getToken() != null)
            .verifyComplete();
    }
}
```

### 4. Profile-Specific Testing

```bash
# Test with JWT profile
./mvnw test -Dspring.profiles.active=jwt

# Test with GCP profile (requires Firebase emulator)
./mvnw test -Dspring.profiles.active=gcp
```

### 5. Running Locally

```bash
# JWT profile (default)
./mvnw spring-boot:run

# GCP profile
SPRING_PROFILES_ACTIVE=gcp \
GCP_PROJECT_ID=your-project \
./mvnw spring-boot:run
```

### 6. Before Committing

**Checklist:**
- [ ] Code compiles without errors
- [ ] All tests pass
- [ ] No sensitive data in code
- [ ] Code follows clean code principles
- [ ] Commit message is descriptive and < 255 chars
- [ ] Changes are minimal and focused

## Adding New Features

### Example: Adding New Authentication Provider

1. **Create domain port** (if needed):
```java
// domain/port/out/AuthenticationProviderPort.java
public interface AuthenticationProviderPort {
    Mono<AuthenticationResult> authenticate(String email, String password);
    Mono<AuthenticationResult> registerUser(RegisterUserData userData);
    Mono<Boolean> validateToken(String token);
    Mono<UserAuthInfo> extractUserInfo(String token);
    boolean managesPasswords();
}
```

2. **Implement adapter:**
```java
// infrastructure/auth/custom/CustomAuthenticationProvider.java
@Component
@Profile("custom")
public class CustomAuthenticationProvider implements AuthenticationProviderPort {
    // Implementation
}
```

3. **Create profile configuration:**
```yaml
# src/main/resources/application-custom.yml
auth:
  provider: custom
app:
  custom:
    config-key: value
```

4. **Add security configuration:**
```java
@Configuration
@Profile("custom")
public class CustomSecurityConfig {
    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        // Configuration
    }
}
```

5. **Update documentation:**
- Add to ARCHITECTURE.md
- Update README.md
- Add example usage

## Common Pitfalls

### 1. Breaking Clean Architecture

❌ **Wrong:**
```java
// Use case depends on infrastructure
public class LoginUserService {
    private final JwtTokenProvider jwtProvider; // Infrastructure leak!
}
```

✅ **Correct:**
```java
// Use case depends on domain port
public class LoginUserService {
    private final AuthenticationProviderPort authProvider; // Domain abstraction
}
```

### 2. Blocking in Reactive Code

❌ **Wrong:**
```java
public Mono<User> findUser(String id) {
    User user = userRepository.findById(id).block(); // BLOCKING!
    return Mono.just(user);
}
```

✅ **Correct:**
```java
public Mono<User> findUser(String id) {
    return userRepository.findById(id); // Reactive chain
}
```

### 3. Not Using Profiles Correctly

❌ **Wrong:**
```java
@Component
public class AuthProvider implements AuthenticationProviderPort {
    // Available in all profiles - causes conflicts!
}
```

✅ **Correct:**
```java
@Component
@Profile("jwt")
public class JwtAuthProvider implements AuthenticationProviderPort {
    // Only active with jwt profile
}
```

## Environment Configuration

### Development

```properties
# application.properties
spring.profiles.active=jwt
logging.level.com.estimate=DEBUG
```

### Production (JWT)

```bash
export SPRING_PROFILES_ACTIVE=jwt
export JWT_SECRET=$(openssl rand -base64 32)
export SPRING_DATA_MONGODB_URI=mongodb://prod-server:27017/estimate
```

### Production (GCP)

```bash
export SPRING_PROFILES_ACTIVE=gcp
export GCP_PROJECT_ID=your-production-project
# GOOGLE_APPLICATION_CREDENTIALS set by Cloud Run automatically
```

## Debugging

### Enable Debug Logging

```properties
logging.level.com.estimate=DEBUG
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.data.mongodb=DEBUG
```

### Common Issues

**Issue:** Tests fail with "No qualifying bean of type 'AuthenticationProviderPort'"
**Solution:** Add `@ActiveProfiles("jwt")` to test class

**Issue:** App fails to start with GCP profile
**Solution:** Ensure GCP_PROJECT_ID is set and Firebase credentials are available

**Issue:** JWT tokens not validating
**Solution:** Check JWT_SECRET is set and consistent

## Performance Optimization

### Reactive Best Practices

- Use `Mono.zip()` for parallel operations
- Avoid `block()` - keep chains reactive
- Use `cache()` for repeated operations
- Set proper timeout values

### MongoDB Optimization

- Create indexes for frequently queried fields
- Use projections to fetch only needed fields
- Leverage reactive drivers fully

## Security Checklist

- [ ] No secrets in code or configuration files
- [ ] Input validation on all endpoints
- [ ] CSRF protection enabled (except for stateless APIs)
- [ ] CORS configured restrictively
- [ ] Error messages don't leak sensitive data
- [ ] Passwords never logged
- [ ] Rate limiting implemented
- [ ] Dependencies regularly updated

## Deployment

### Build for Production

```bash
# Build JAR
./mvnw clean package -DskipTests

# Build Docker image
docker build -t estimate-backend:latest .

# Push to registry
docker tag estimate-backend:latest gcr.io/PROJECT_ID/estimate-backend:latest
docker push gcr.io/PROJECT_ID/estimate-backend:latest
```

### Deploy to Cloud Run

```bash
# JWT Authentication
gcloud run deploy estimate-backend \
  --image gcr.io/PROJECT_ID/estimate-backend:latest \
  --set-env-vars="SPRING_PROFILES_ACTIVE=jwt,JWT_SECRET=$JWT_SECRET"

# GCP Identity Platform
gcloud run deploy estimate-backend \
  --image gcr.io/PROJECT_ID/estimate-backend:latest \
  --set-env-vars="SPRING_PROFILES_ACTIVE=gcp,GCP_PROJECT_ID=$PROJECT_ID"
```

## Important Guidelines

- ✅ Always use GitHub CLI for Git operations
- ✅ Always run tests before committing
- ✅ Always fix detected errors before committing
- ✅ Always review code changes before committing
- ✅ Always follow DDD principles during implementation
- ✅ Always commit changes to Git repository
- ✅ Always push changes to remote repository
- ✅ Always follow small commits practices
- ✅ Use trunk-based development (no long-lived feature branches)
- ✅ Use hexagonal architecture patterns
- ✅ Keep domain layer pure (no framework dependencies)
- ✅ Profile-specific components must use `@Profile` annotation
- ✅ Never commit sensitive data (secrets, keys, credentials)

## Resources

- [README.md](README.md) - Getting started and API documentation
- [ARCHITECTURE.md](ARCHITECTURE.md) - Detailed architecture and authentication design
- [Spring WebFlux Docs](https://docs.spring.io/spring-framework/reference/web/webflux.html)
- [Project Reactor](https://projectreactor.io/docs/core/release/reference/)
- [Firebase Admin SDK](https://firebase.google.com/docs/admin/setup)
