-- V9: Convert custom Postgres enum columns to VARCHAR for Spring Data JPA / Hibernate compatibility

ALTER TABLE complaints ALTER COLUMN category TYPE VARCHAR(50) USING category::VARCHAR;
ALTER TABLE complaints ALTER COLUMN priority TYPE VARCHAR(50) USING priority::VARCHAR;
ALTER TABLE complaints ALTER COLUMN status TYPE VARCHAR(50) USING status::VARCHAR;

ALTER TABLE complaint_status_history ALTER COLUMN from_status TYPE VARCHAR(50) USING from_status::VARCHAR;
ALTER TABLE complaint_status_history ALTER COLUMN to_status TYPE VARCHAR(50) USING to_status::VARCHAR;
