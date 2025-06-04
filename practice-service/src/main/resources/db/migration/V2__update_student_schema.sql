-- Update practice_students table
ALTER TABLE practice_students RENAME COLUMN user_id TO student_id;

-- Update students table (no changes needed as we're using id directly)
-- The id column is already the primary key and will be used as both id and userId 