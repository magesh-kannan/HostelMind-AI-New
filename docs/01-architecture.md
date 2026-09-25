# HostelMind-AI Architecture & System Design

## 1. Overview
HostelMind-AI is a production-grade, AI-powered hostel operations platform designed for educational institutions.
The system follows a **Modular Monolith** pattern built with **Clean Architecture** principles.

---

## 2. Layered Clean Architecture

```
com.hostelmind
 ├── domain               # Pure Java entities, value objects, domain events, repository interfaces
 ├── application          # Use cases, application services, DTOs, input validation, AI orchestration ports
 ├── infrastructure       # Spring Data JPA, Security/JWT, LLM adapters (LangChain4j), Storage adapters, Email adapters
 └── interface            # REST controllers, WebSocket STOMP endpoints, Global Exception Handler, OpenAPI specs
```

### Layer Constraints & Rules
1. **Domain Layer:** Has ZERO dependencies on frameworks (no Spring, no Hibernate annotations on core domain objects if decoupled, or clean JPA entities with JPA standard annotations only).
2. **Application Layer:** Orchestrates business flows. Implements Use Cases. Depends ONLY on Domain interfaces.
3. **Infrastructure Layer:** Implements interfaces defined in Domain and Application layers (Repository implementations, AI adapters, File storage adapters).
4. **Interface Layer:** Handles HTTP/WebSocket requests, maps DTOs, delegates to Application Use Cases. No business logic in controllers. Enforces server-side RBAC.

---

## 3. Technology Stack & Zero-Cost Deployment Topology

- **Backend:** Java 21, Spring Boot 3.4.3, Spring Security (JWT + Refresh Tokens), Spring Data JPA, Flyway DB Migrations, Lombok, Maven.
- **Frontend:** React 18, TypeScript, Vite, Material UI (Custom Theme System), React Router v6, TanStack Query v5, Axios, React Hook Form + Zod.
- **Database:** PostgreSQL 16 + `pgvector` extension for vector embeddings & RAG.
- **AI Framework:** LangChain4j behind an `AiProviderPort` abstraction.

### Zero-Cost Cloud Infrastructure Mapping
- **Frontend SPA:** Hosted on **Vercel** (Hobby Free Tier).
- **Backend Service:** Hosted on **Render** (Free Web Service Docker container).
  - Memory tuning: `-XX:+UseContainerSupport -XX:MaxRAMPercentage=70 -XX:MaxMetaspaceSize=96m`
  - Lazy initialization enabled to fit under 512MB RAM budget.
- **Database:** **Neon / Supabase** PostgreSQL with permanent free tier + `pgvector`.
- **File Storage:** **Cloudinary / Supabase Storage** free tier behind `FileStoragePort`.
- **Messaging/Email:** SMTP / Resend free tier behind `NotificationPort`.
