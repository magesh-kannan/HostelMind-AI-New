-- V10: Seed default institutional baseline infrastructure (Campus, Hostel, Block, Floor, Room, Bed)

INSERT INTO campuses (id, name, code, address, created_at, updated_at)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'Main Institutional Campus',
    'MAIN-CAMPUS-01',
    '100 University Avenue, Tech Park',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (code) DO NOTHING;

INSERT INTO hostels (id, campus_id, name, gender_type, warden_id, created_at, updated_at)
VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    'Alpha Resident Hostel',
    'COED',
    NULL,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO blocks (id, hostel_id, name, code, total_floors, created_at, updated_at)
VALUES (
    '33333333-3333-3333-3333-333333333333',
    '22222222-2222-2222-2222-222222222222',
    'Block A',
    'BLK-A',
    1,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO floors (id, block_id, floor_number, name, created_at, updated_at)
VALUES (
    '44444444-4444-4444-4444-444444444444',
    '33333333-3333-3333-3333-333333333333',
    1,
    'First Floor',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO rooms (id, floor_id, room_number, room_type, capacity, occupied_count, status, monthly_rent, created_at, updated_at)
VALUES (
    '55555555-5555-5555-5555-555555555555',
    '44444444-4444-4444-4444-444444444444',
    '101',
    'DOUBLE',
    2,
    0,
    'AVAILABLE',
    5000.00,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;

INSERT INTO beds (id, room_id, bed_number, status, created_at, updated_at)
VALUES (
    '66666666-6666-6666-6666-666666666666',
    '55555555-5555-5555-5555-555555555555',
    'Bed 101-A',
    'VACANT',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
), (
    '77777777-7777-7777-7777-777777777777',
    '55555555-5555-5555-5555-555555555555',
    'Bed 101-B',
    'VACANT',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
)
ON CONFLICT (id) DO NOTHING;
