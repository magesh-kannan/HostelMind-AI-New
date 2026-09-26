-- V7: Facilities, Maintenance & Emergency SOS Schema

-- 1. Facility Assets Table
CREATE TABLE facility_assets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(50) NOT NULL,
    location VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPERATIONAL',
    last_inspected_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_facility_assets_hostel ON facility_assets(hostel_id);
CREATE INDEX idx_facility_assets_status ON facility_assets(status);

-- 2. Maintenance Schedules Table
CREATE TABLE maintenance_schedules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    asset_id UUID NOT NULL REFERENCES facility_assets(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    frequency VARCHAR(50) NOT NULL DEFAULT 'MONTHLY',
    assigned_technician VARCHAR(255),
    next_due_date DATE NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_maintenance_schedules_asset ON maintenance_schedules(asset_id);
CREATE INDEX idx_maintenance_schedules_due ON maintenance_schedules(next_due_date);

-- 3. Emergency SOS Alerts Table
CREATE TABLE emergency_sos_alerts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hostel_id UUID REFERENCES hostels(id) ON DELETE SET NULL,
    room_number VARCHAR(50),
    sos_type VARCHAR(50) NOT NULL,
    location_details VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'TRIGGERED',
    triggered_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    resolved_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_sos_alerts_student ON emergency_sos_alerts(student_id);
CREATE INDEX idx_sos_alerts_status ON emergency_sos_alerts(status);
CREATE INDEX idx_sos_alerts_triggered ON emergency_sos_alerts(triggered_at);
