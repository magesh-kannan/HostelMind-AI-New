# 🏨🤖 HostelMind AI — Enterprise Institutional Hostel Management Platform

> **A Next-Generation, Production-Grade Hostel & Campus Management Platform built with Clean Architecture, Java 21, Spring Boot 3.4, React 18, PostgreSQL, and LangChain4j AI RAG Engine.**

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Java 21](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-green.svg)](https://spring.io/projects/spring-boot)
[![React 18](https://img.shields.io/badge/React-18.3-blue.svg)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-5.7-blue.svg)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-6.1-purple.svg)](https://vitejs.dev/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16%20%2B%20pgvector-blue.svg)](https://www.postgresql.org/)

---

## 🌐 Live Deployed Application

- 🚀 **Frontend Live Site (Vercel):** [https://hostel-mind-ai-new.vercel.app](https://hostel-mind-ai-new.vercel.app/)
- ⚙️ **Backend API Endpoint (Render):** [https://hostelmind-ai-new.onrender.com/api/v1](https://hostelmind-ai-new.onrender.com/api/v1)
- 🏥 **Backend Health Check:** [https://hostelmind-ai-new.onrender.com/actuator/health](https://hostelmind-ai-new.onrender.com/actuator/health)

---

## 🔑 Demo Access Credentials

| Role | Email | Password | Dashboard Access |
|------|-------|----------|------------------|
| 🛠️ **System Admin** | `admin@hostelmind.ai` | `Admin@123` | Full System Control, Audits, Billing & Infrastructure |
| 🛡️ **Hostel Warden** | `warden@hostelmind.ai` | `Warden@123` | Room Allocations, Complaint Resolution, Mess & SOS |
| 🎓 **Resident Student** | `student@hostelmind.ai` | `Student@123` | Digital ID, Complaint Filing, QR Mess Passes & AI Chat |

---

## 🌟 Key Architecture & Tech Stack

```
com.hostelmind
 ├── domain               # Pure Java entities, value objects, domain events, repository interfaces
 ├── application          # Use cases, application services, DTOs, input validation, AI orchestration ports
 ├── infrastructure       # Spring Data JPA, Security/JWT, LLM adapters (LangChain4j), Storage adapters
 └── interface            # REST controllers, WebSocket STOMP endpoints, Global Exception Handler
```

### **Backend Stack**
- **Language & Runtime:** Java 21 LTS
- **Framework:** Spring Boot 3.4.3
- **Architecture Pattern:** Clean Architecture / Hexagonal Architecture (Domain Ports & Adapters)
- **Database:** PostgreSQL 16 + `pgvector` extension for AI vector embeddings
- **Database Migrations:** Flyway Schema Migrations (`V1` to `V10`)
- **Security:** Spring Security with Stateless JWT Authentication + Role-Based Access Control (RBAC)
- **AI & RAG Engine:** LangChain4j + Spring AI Integration with vector similarity search
- **Unit Testing:** JUnit 5, Mockito (62 passing automated unit tests)

### **Frontend Stack**
- **Framework:** React 18, TypeScript, Vite 6
- **UI System:** Material-UI (MUI v5) + Glassmorphic Custom CSS Theme
- **State & Queries:** TanStack React Query v5 + Axios Client with JWT Interceptors
- **Form Handling & Validation:** React Hook Form + Zod
- **Icons:** Lucide-React & MUI Icons
- **Theme:** Dynamic Light & Dark Mode with institutional aesthetic tokens

---

## 🚀 Modules & Capabilities

| Module | Core Features | RBAC Scope |
|--------|---------------|------------|
| 🏢 **Infrastructure & Rooms** | Campus, Hostel, Block, Floor, Room, and Bed hierarchy management. Real-time room allocation & transfer. | Admin, Warden |
| 🛠️ **Complaint Management** | Automated category & priority classification, complaint assignment, resolution tracking, and status audit logs. | Student, Warden, Admin |
| 💳 **Billing & Invoices** | Academic year fee structures, automatic student invoice generation, itemized charges, and payment gateway integration. | Admin, Warden, Student |
| 🍽️ **Mess Operations** | Daily/Weekly menu timetable planner, QR code meal pass generation, real-time scanner attendance, and meal feedback system. | Warden, Student |
| 🚨 **Facilities & Emergency SOS** | Institutional asset tracking, maintenance schedules, and one-tap instant Emergency SOS alerts with response workflows. | Student, Warden, Admin |
| 🤖 **AI Assistant & RAG** | AI Concierge powered by LangChain4j with document vector store (`pgvector`) for instant institutional query answering. | All Roles |
| 🪪 **Digital Student ID** | Verification-ready digital hostel ID card with QR code, emergency contacts, and active room assignment details. | Student |
| 📜 **Audit Logs** | Immutable system-wide audit trail capturing security events, data mutations, allocations, and administrative actions. | Admin |
| 📣 **Community Noticeboard** | Institutional announcements, broadcasts, tag filtering, and interactive reactions. | Student, Warden, Admin |

---

## 🗄️ Database Schema Migrations (Flyway)

- `V1__initial_schema.sql`: Core schema (Users, Hostels, Blocks, Floors, Rooms, Beds, Allocations, Complaints, Status History)
- `V2__ai_agent_schema.sql`: AI agent execution logs and session tracking
- `V3__knowledge_vector_schema.sql`: RAG Knowledge documents and `pgvector` embeddings schema
- `V4__billing_schema.sql`: Fee structures, invoices, items, and payments schema
- `V5__mess_schema.sql`: Mess menus, QR attendance tokens, and student feedback schema
- `V6__mess_attendance_unique.sql`: Daily meal attendance uniqueness constraints
- `V7__facility_emergency_schema.sql`: Facility assets, maintenance schedules, and emergency SOS alerts
- `V8__announcements_schema.sql`: Community noticeboard broadcasts and user reaction system
- `V9__seed_demo_accounts.sql`: Demo user account initialization (Admin, Warden, Student)
- `V10__seed_default_institutional_data.sql`: Institutional infrastructure seeding (Campus, Hostel, Block, Floor, Room, Beds)

---

## 🧪 Testing & Verification Status

### **Backend Test Suite (JUnit 5 + Mockito)**
```bash
cd backend
mvn test
```
- **Result:** `62 tests run, 0 failures, 0 errors, 100% pass rate.`

### **Frontend Type Check (TypeScript)**
```bash
cd frontend
npx tsc --noEmit
```
- **Result:** `0 TypeScript compilation errors.`

---

## 🛠️ Local Installation & Setup

### Prerequisites
- Java 21 JDK
- Node.js (v18+) & `npm`
- PostgreSQL 16+ (or Docker for local Postgres)

### 1. Clone Repository
```bash
git clone https://github.com/magesh-kannan/HostelMind-AI-New.git
cd HostelMind-AI-New
```

### 2. Run Backend
```bash
cd backend
mvn spring-boot:run
```
Backend API will start at `http://localhost:8080/api/v1`.

### 3. Run Frontend
```bash
cd frontend
npm install
npm run dev
```
Frontend Web Client will open at `http://localhost:5173`.

---

## 📄 License
This project is licensed under the [MIT License](LICENSE).