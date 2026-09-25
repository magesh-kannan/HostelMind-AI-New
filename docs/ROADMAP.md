# HostelMind-AI — Master Execution Roadmap & Phase Tracker

## Status Summary
- **Current Phase:** Phase 0 (Repo Scaffolding & Foundational Architecture)
- **Repository State:** Clean monorepo structure with `backend/` (Spring Boot 3.4/Java 21) and `frontend/` (React/TypeScript/MUI).
- **Deployment Strategy:** Vercel (Frontend SPA) + Render Free Web Service (Backend Docker) + Neon/Supabase (PostgreSQL + pgvector). Zero-cost infra.

---

## Roadmap Phases

| Phase | Description | Status | Verification Criteria |
|---|---|---|---|
| **Phase 0** | **Repo Scaffolding & Architecture**<br>- Spring Boot 3.4 / Java 21 Clean Architecture<br>- React 18 / Vite / TypeScript / MUI v6 Design System<br>- JWT Auth + Refresh Tokens + RBAC<br>- Postgres + Flyway baseline migration (`V1__initial_schema.sql`)<br>- Audit Logging & AI Provider Interface Abstraction<br>- Docker Compose & GitHub Actions CI | 🟡 In Progress | Clean `mvn clean verify` & `npm run build`, live DB roundtrip test, JWT auth flow test |
| **Phase 1** | **Hostel Infrastructure & Allocation Engine**<br>- Campus, Hostel, Block, Floor, Room, Bed models<br>- Room allocation, transfers, vacancy tracking<br>- Allocation history, capacity & duplicate-allocation guards | 🔴 Pending | Integration tests for double-booking prevention, room occupancy stats API |
| **Phase 2** | **Complaint Management & Hardened Lifecycle**<br>- Full lifecycle state machine (NEW -> CLASSIFIED -> PRIORITIZED -> ASSIGNED -> IN_PROGRESS -> WAITING_FOR_STUDENT -> RESOLVED -> VERIFIED -> CLOSED)<br>- File attachment port (Cloudinary/Supabase adapter)<br>- State audit history & reopening rules | 🔴 Pending | State transition rules unit tests, file upload roundtrip, status history audit |
| **Phase 3** | **Multi-Agent AI Pipeline & RAG System**<br>- 10 AI Agents: Classification, Priority, Duplicate, Routing, SLA Resolution, Sentiment, Notification, Knowledge Assistant (RAG/pgvector), Administrative Insights, Maintenance Assignment<br>- Orchestration pipeline with warden approval gates<br>- `ai_agent_runs` & `ai_decisions` audit tables | 🔴 Pending | AI agent runs logged to DB, pgvector RAG query test, human approval gate flow |
| **Phase 4** | **Fees, Payments & Invoicing**<br>- Fee structures, invoices, payment records (gateway port)<br>- Receipts, reminders, overdue tracking | 🔴 Pending | Fee calculation tests, invoice generation, payment status transitions |
| **Phase 5** | **Mess Operations & AI Food Forecasting**<br>- 2-week menu planner, student dietary preferences<br>- Expected/actual meal count tracking<br>- Mess Forecasting Agent & Inventory Agent | 🔴 Pending | Menu CRUD, meal count prediction accuracy logging, inventory deduction logic |
| **Phase 6** | **Facility Management & Emergency Coordination**<br>- Water/RO/Restroom/Laundry status & student reports<br>- Emergency Coordination Agent<br>- Privacy-conscious live location sharing with TTL & explicit toggle | 🔴 Pending | Facility status update flow, Emergency notification broadcast, location purge cron |
| **Phase 7** | **Real-Time Communication & Announcements**<br>- WebSocket / STOMP multi-channel chat<br>- Communities/groups & moderation tools<br>- In-app, email, SMS notification abstraction | 🔴 Pending | Socket-to-socket message delivery test, announcement RBAC verification |
| **Phase 8** | **Role-Tailored Dashboards & Global Search**<br>- Student, Warden, Higher Official customized analytics<br>- Real DB queries (zero static dummy data)<br>- Global SQL + pgvector hybrid semantic search | 🔴 Pending | SQL aggregate query benchmarks, dashboard data accuracy audit |
| **Phase 9** | **Digital Student/Staff IDs & Onboarding**<br>- High-contrast institutional Digital ID cards with QR code<br>- Distinct visual styles for Student vs Staff<br>- Profile photo upload, crop, and validation | 🔴 Pending | Digital ID visual verification, QR code verification |
| **Phase 10**| **Security Hardening & Report Generation**<br>- BCrypt / Argon2 password hashing<br>- OTP resend cooldown & attempt limiting<br>- Global rate limiting & security audit logs<br>- PDF/CSV report exports | 🔴 Pending | Rate limiting integration test, security audit report export test |
| **Phase 11**| **Comprehensive E2E Testing Pass**<br>- Unit, Integration (Testcontainers/H2), E2E UI flows | 🔴 Pending | 100% build pass, test suite execution report |
| **Phase 12**| **Production Readiness & Zero-Cost Deployment**<br>- Spring Actuator tuning & JVM memory flags (`-XX:MaxRAMPercentage=70`)<br>- README, ERD diagrams, Vercel + Render + Neon deployment validation | 🔴 Pending | Live production URL check, zero budget verification |
