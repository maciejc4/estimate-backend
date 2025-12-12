# Desired Architecture: Microservices

This document outlines the plan to evolve the current monolithic backend into a microservices architecture.

## Current State

```
┌─────────────────────────────────────────────────┐
│              estimate-backend                    │
│                 (Monolith)                       │
├─────────────────────────────────────────────────┤
│  ┌─────────┐ ┌──────────┐ ┌─────────────────┐  │
│  │  Auth   │ │  Works   │ │   Templates     │  │
│  └─────────┘ └──────────┘ └─────────────────┘  │
│  ┌─────────┐ ┌──────────┐ ┌─────────────────┐  │
│  │  Users  │ │Estimates │ │     Admin       │  │
│  └─────────┘ └──────────┘ └─────────────────┘  │
├─────────────────────────────────────────────────┤
│                   MongoDB                        │
└─────────────────────────────────────────────────┘
```

## Target State

```
                    ┌─────────────────┐
                    │   API Gateway   │
                    │  (Cloud Run)    │
                    └────────┬────────┘
                             │
        ┌────────────────────┼────────────────────┐
        │                    │                    │
        ▼                    ▼                    ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│ Auth Service  │   │ Works Service │   │Estimate Svc   │
│               │   │               │   │               │
│ - Login       │   │ - CRUD Works  │   │ - CRUD Est.   │
│ - Register    │   │ - Materials   │   │ - Calculations│
│ - Token mgmt  │   │               │   │ - Templates   │
└───────┬───────┘   └───────┬───────┘   └───────┬───────┘
        │                   │                   │
        ▼                   ▼                   ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│ Firebase/JWT  │   │   MongoDB     │   │   MongoDB     │
│ or MongoDB    │   │   (Works)     │   │  (Estimates)  │
└───────────────┘   └───────────────┘   └───────────────┘
```

## Proposed Microservices

### 1. Auth Service
**Responsibility:** Authentication and user management

```yaml
Service: auth-service
Endpoints:
  - POST /auth/register
  - POST /auth/login
  - POST /auth/refresh
  - GET /auth/validate
  - GET /users/me
  - PUT /users/me
  - DELETE /users/me
Database: Firebase (GCP) or MongoDB (JWT)
```

**Bounded Context:** Identity & Access

### 2. Works Service  
**Responsibility:** Manage construction works and materials

```yaml
Service: works-service
Endpoints:
  - GET /works
  - POST /works
  - GET /works/{id}
  - PUT /works/{id}
  - DELETE /works/{id}
Database: MongoDB (works collection)
```

**Bounded Context:** Work Catalog

### 3. Estimate Service
**Responsibility:** Cost estimates and templates

```yaml
Service: estimate-service
Endpoints:
  - GET /templates
  - POST /templates
  - GET /estimates
  - POST /estimates
  - PUT /estimates/{id}
  - DELETE /estimates/{id}
  - GET /estimates/{id}/calculate
Database: MongoDB (templates, estimates collections)
```

**Bounded Context:** Estimation

### 4. API Gateway
**Responsibility:** Routing, auth validation, rate limiting

```yaml
Service: api-gateway
Technology: Spring Cloud Gateway or GCP API Gateway
Features:
  - Route requests to services
  - Validate JWT/Firebase tokens
  - Rate limiting
  - CORS handling
  - Request logging
```

## Service Communication

### Synchronous (HTTP/gRPC)
- Gateway → Services
- Estimate Service → Works Service (get work details for calculation)

### Asynchronous (Pub/Sub)
- Auth Service → All Services (user deleted event)
- Works Service → Estimate Service (work updated/deleted event)

```
┌───────────────┐     ┌──────────────┐
│ Auth Service  │────▶│   Pub/Sub    │
└───────────────┘     │  (Events)    │
                      └──────┬───────┘
                             │
              ┌──────────────┼──────────────┐
              ▼              ▼              ▼
        ┌───────────┐ ┌───────────┐ ┌───────────┐
        │  Works    │ │ Estimate  │ │   Other   │
        │  Service  │ │  Service  │ │  Services │
        └───────────┘ └───────────┘ └───────────┘
```

## Data Management

### Database per Service
Each service owns its data:

| Service | Database | Collections |
|---------|----------|-------------|
| Auth | Firebase/MongoDB | users |
| Works | MongoDB | works |
| Estimate | MongoDB | templates, estimates |

### Data Consistency
- Eventual consistency via events
- Saga pattern for cross-service transactions
- Each service maintains denormalized data it needs

### Example: Creating Estimate
```
1. Client → Gateway → Estimate Service: Create estimate
2. Estimate Service → Works Service: Get work details (sync)
3. Estimate Service: Calculate totals
4. Estimate Service → MongoDB: Save estimate
5. Estimate Service → Pub/Sub: EstimateCreated event
```

## Migration Strategy

### Phase 1: Extract Auth Service
```
Current:
  estimate-backend (all)
  
Target:
  auth-service (auth, users)
  estimate-backend (works, templates, estimates)
```

1. Extract authentication logic to separate service
2. Add API Gateway for routing
3. Keep other functionality in monolith
4. Test thoroughly

### Phase 2: Extract Works Service
```
Current:
  auth-service
  estimate-backend (works, templates, estimates)
  
Target:
  auth-service
  works-service
  estimate-backend (templates, estimates)
```

1. Extract works management
2. Add inter-service communication
3. Implement work data caching in estimate service

### Phase 3: Finalize Estimate Service
```
Final:
  auth-service
  works-service
  estimate-service
  api-gateway
```

1. Rename remaining monolith to estimate-service
2. Remove unused code
3. Optimize inter-service communication
4. Add event-driven updates

## Technology Stack

### Per Service
- Java 21 / Spring Boot 3.4 / WebFlux
- Docker container
- Cloud Run deployment
- Individual MongoDB database

### Shared Infrastructure
- GCP API Gateway or Spring Cloud Gateway
- GCP Pub/Sub for events
- Cloud Logging / Monitoring
- Secret Manager for credentials

### Observability
- Distributed tracing (Cloud Trace)
- Centralized logging
- Health checks per service
- Metrics and alerting

## Repository Structure

### Option A: Monorepo
```
estimate-platform/
├── services/
│   ├── auth-service/
│   ├── works-service/
│   ├── estimate-service/
│   └── api-gateway/
├── shared/
│   ├── common-models/
│   └── common-utils/
└── infrastructure/
    ├── terraform/
    └── kubernetes/
```

### Option B: Multi-repo (Recommended)
```
Repositories:
  - estimate-auth-service
  - estimate-works-service  
  - estimate-estimate-service
  - estimate-api-gateway
  - estimate-shared-lib
  - estimate-infrastructure
```

## API Versioning

```
/api/v1/works
/api/v2/works  (future)
```

Gateway routes to appropriate service version.

## Security

### Service-to-Service Authentication
- Internal JWT tokens
- Service accounts in GCP
- mTLS for sensitive services

### External Authentication
- Gateway validates all external tokens
- Services trust gateway headers
- User context propagated via headers

## Cost Estimation

### Cloud Run (per service)
- Min instances: 0 (scale to zero)
- Max instances: 10
- Memory: 512MB - 1GB
- CPU: 1 vCPU

### MongoDB Atlas
- Shared cluster for dev
- Dedicated cluster for prod
- Per-service databases

### Estimated Monthly Cost (GCP)
| Resource | Dev | Prod |
|----------|-----|------|
| Cloud Run (4 services) | $20 | $100 |
| MongoDB Atlas | Free | $50 |
| Pub/Sub | $1 | $10 |
| API Gateway | $5 | $20 |
| **Total** | **~$26** | **~$180** |

## Success Criteria

- [ ] Each service deployable independently
- [ ] No shared database between services
- [ ] Circuit breakers for service calls
- [ ] Graceful degradation
- [ ] Zero-downtime deployments
- [ ] Horizontal scaling capability
- [ ] Centralized logging and tracing
- [ ] <100ms latency for 95% of requests

## Timeline

| Phase | Duration | Deliverables |
|-------|----------|--------------|
| 1. Auth Service | 2 weeks | Extracted auth, gateway |
| 2. Works Service | 2 weeks | Extracted works, events |
| 3. Estimate Service | 1 week | Finalized architecture |
| 4. Optimization | 1 week | Performance, monitoring |
| **Total** | **6 weeks** | Full microservices |

## Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| Increased complexity | Start with 3 services only |
| Network latency | Cache frequently used data |
| Data consistency | Use saga pattern, eventual consistency |
| Debugging difficulty | Invest in observability |
| Cost increase | Use scale-to-zero, shared resources |

## Decision Log

| Date | Decision | Rationale |
|------|----------|-----------|
| TBD | Multi-repo vs monorepo | TBD after team discussion |
| TBD | gRPC vs REST | TBD based on performance needs |
| TBD | Shared DB vs per-service | Per-service for true isolation |

---

**Status:** Proposal  
**Next Step:** Team review and approval
