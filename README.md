# HostelMind-AI 🏨🤖

> **Next-Generation Institutional Hostel Management Platform powered by Clean Architecture, LangChain4j RAG, and Modern React.**

---

## 🌟 Architecture & Tech Stack

### **Backend Stack**
- **Core Engine:** Java 21, Spring Boot 3.4
- **Architecture Pattern:** Clean Architecture / Hexagonal Architecture (Domain Ports & Adapters)
- **Database:** PostgreSQL with `pgvector` extension for vector embeddings
- **Schema Management:** Flyway Migrations (V1 to V8)
- **Security:** Spring Security + JWT Stateless Authentication
- **AI Engine:** LangChain4j + Dev-friendly local fallback orchestrator
- **Testing:** JUnit 5, Mockito (62 Automated Unit Tests)

### **Frontend Stack**
- **Framework:** React 18, TypeScript, Vite
- **UI & Styling:** Material-UI (MUI v5) + Glassmorphic Custom CSS Design System
- **Icons:** Lucide-React + MUI Icons
- **State & Data Fetching:** TanStack React Query + Axios Client with JWT Interceptors
- **Theme:** Dynamic Light & Dark Mode Support

---

## 🚀 Completed Functional Modules

| Phase | Module | Backend Ports & Services | DB Migrations | Frontend Interface |
|-------|--------|--------------------------|---------------|--------------------|
| **1-2** | **Hostel & Complaints** | `HostelStructureUseCase`, `ComplaintUseCase` | `V1` | `/rooms`, `/allocations`, `/complaints` |
| **3** | **AI Agent & RAG Engine** | `AiAgentUseCase`, `KnowledgeDocument` | `V2`, `V3` | `/ai` |
| **4** | **Billing & Payments** | `BillingUseCase`, Invoice & Payment Gateways | `V4` | `/fees` |
| **5** | **Mess Operations** | `MessUseCase`, Menu, QR Tokens & Attendance | `V5`, `V6` | `/mess` |
| **6** | **Facilities & Emergency SOS** | `FacilityEmergencyUseCase`, Assets & SOS Alerts | `V7` | `/facilities` |
| **7** | **Audit Logs & Digital ID** | `AuditUseCase`, User Directory & Audit Trail | Schema | `/audit`, `/digital-id` |
| **8** | **Noticeboard & Broadcasts** | `CommunityUseCase`, Announcements & Reactions | `V8` | `/chat` |

---

## 📊 Database Schema Migrations

- `V1__initial_schema.sql`: Core schema (Users, Hostels, Blocks, Floors, Rooms, Beds, Allocations, Complaints, Status History)
- `V2__ai_agent_schema.sql`: AI agent runs, execution logs, and prompt session tracking
- `V3__knowledge_vector_schema.sql`: RAG Knowledge documents and pgvector vector embeddings schema
- `V4__billing_schema.sql`: Fee structures, invoices, items, and payments schema
- `V5__mess_schema.sql`: Mess menus, QR attendance tokens, and student feedback schema
- `V6__mess_attendance_unique.sql`: Daily meal attendance uniqueness constraints
- `V7__facility_emergency_schema.sql`: Facility assets, maintenance schedules, and SOS alert system
- `V8__announcements_schema.sql`: Community noticeboard broadcasts and user reaction system

---

## 🧪 Testing & Verification

### **Run Backend Tests**
```bash
cd backend
mvn test
```
*Current Coverage:* **62 tests pass, 0 failures, 0 errors**.

### **Run Frontend Type Check**
```bash
cd frontend
npx tsc --noEmit
```
*Current Verification:* **0 TypeScript errors**.

---

## 🏃 Running Locally

### **Backend Service**
```bash
cd backend
mvn spring-boot:run
```
Running on port `8080` at `http://localhost:8080/api/v1`.

### **Frontend Client**
```bash
cd frontend
npm install
npm run dev
```
Running on port `5173` at `http://localhost:5173`.