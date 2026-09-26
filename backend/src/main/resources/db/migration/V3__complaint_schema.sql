-- Phase 2: Complaint Management Schema

-- Complaint status enum
CREATE TYPE complaint_status AS ENUM (
    'NEW', 'CLASSIFIED', 'PRIORITIZED', 'ASSIGNED',
    'IN_PROGRESS', 'WAITING_FOR_STUDENT', 'RESOLVED',
    'VERIFIED', 'CLOSED', 'REOPENED'
);

-- Complaint priority enum
CREATE TYPE complaint_priority AS ENUM (
    'LOW', 'MEDIUM', 'HIGH', 'URGENT', 'CRITICAL'
);

-- Complaint category enum
CREATE TYPE complaint_category AS ENUM (
    'ELECTRICAL', 'PLUMBING', 'FURNITURE', 'HOUSEKEEPING',
    'INTERNET_CONNECTIVITY', 'SECURITY', 'FOOD_QUALITY',
    'NOISE', 'PEST_CONTROL', 'AC_COOLING', 'WATER_SUPPLY',
    'LAUNDRY', 'MEDICAL', 'ADMINISTRATION', 'OTHER'
);

-- Main complaints table
CREATE TABLE complaints (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id      UUID NOT NULL REFERENCES users(id),
    hostel_id       UUID NOT NULL REFERENCES hostels(id),
    room_id         UUID REFERENCES rooms(id),
    assigned_to_id  UUID REFERENCES users(id),

    title           VARCHAR(255) NOT NULL,
    description     TEXT NOT NULL,
    category        complaint_category NOT NULL,
    priority        complaint_priority NOT NULL DEFAULT 'MEDIUM',
    status          complaint_status  NOT NULL DEFAULT 'NEW',

    attachment_url  VARCHAR(500),
    sla_deadline    TIMESTAMPTZ,
    reopen_count    INT NOT NULL DEFAULT 0,

    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    resolved_at     TIMESTAMPTZ,
    closed_at       TIMESTAMPTZ
);

CREATE INDEX idx_complaints_student_id    ON complaints(student_id);
CREATE INDEX idx_complaints_hostel_id     ON complaints(hostel_id);
CREATE INDEX idx_complaints_status        ON complaints(status);
CREATE INDEX idx_complaints_assigned_to   ON complaints(assigned_to_id);
CREATE INDEX idx_complaints_category      ON complaints(category);

-- Complaint status history (immutable audit trail)
CREATE TABLE complaint_status_history (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    complaint_id        UUID NOT NULL REFERENCES complaints(id) ON DELETE CASCADE,
    changed_by_user_id  UUID REFERENCES users(id),

    from_status         complaint_status,
    to_status           complaint_status NOT NULL,
    note                TEXT,

    changed_at          TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_csh_complaint_id ON complaint_status_history(complaint_id);
CREATE INDEX idx_csh_changed_at   ON complaint_status_history(changed_at);
