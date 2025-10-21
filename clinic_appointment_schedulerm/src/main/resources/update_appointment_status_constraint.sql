-- Update appointment status constraint to allow all required statuses
-- Run this SQL script on your PostgreSQL database

ALTER TABLE appointments DROP CONSTRAINT IF EXISTS appointments_status_check;
ALTER TABLE appointments
  ADD CONSTRAINT appointments_status_check
  CHECK (status IN ('PENDING','SCHEDULED','COMPLETED','CANCELLED'));