-- V6: Mess Operations & Meal Attendance Schema

-- 1. Mess Menus Table
CREATE TABLE mess_menus (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hostel_id UUID NOT NULL REFERENCES hostels(id) ON DELETE CASCADE,
    day_of_week VARCHAR(20) NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    items TEXT NOT NULL,
    calorie_count INT DEFAULT 0,
    special_notes VARCHAR(255),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_hostel_day_meal UNIQUE (hostel_id, day_of_week, meal_type)
);

CREATE INDEX idx_mess_menus_hostel ON mess_menus(hostel_id);

-- 2. Meal Attendance Table (QR Scanner Pass)
CREATE TABLE meal_attendance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hostel_id UUID REFERENCES hostels(id) ON DELETE SET NULL,
    meal_type VARCHAR(20) NOT NULL,
    attendance_date DATE NOT NULL,
    qr_token VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'SERVED',
    scanned_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT uq_student_meal_date UNIQUE (student_id, meal_type, attendance_date)
);

CREATE INDEX idx_meal_attendance_student ON meal_attendance(student_id);
CREATE INDEX idx_meal_attendance_date ON meal_attendance(attendance_date);
CREATE INDEX idx_meal_attendance_token ON meal_attendance(qr_token);

-- 3. Mess Feedback Table
CREATE TABLE mess_feedback (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    menu_id UUID REFERENCES mess_menus(id) ON DELETE SET NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE INDEX idx_mess_feedback_student ON mess_feedback(student_id);
CREATE INDEX idx_mess_feedback_menu ON mess_feedback(menu_id);
