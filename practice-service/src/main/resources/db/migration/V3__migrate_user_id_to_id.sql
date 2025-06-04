-- Backup data from students table
CREATE TABLE students_backup AS SELECT * FROM students;

-- Backup data from instructors table
CREATE TABLE instructors_backup AS SELECT * FROM instructors;

-- Update students table
-- First, ensure id column is not null and unique
ALTER TABLE students ALTER COLUMN id SET NOT NULL;
ALTER TABLE students ADD CONSTRAINT students_id_unique UNIQUE (id);

-- Copy data from userId to id where id is null
UPDATE students 
SET id = user_id 
WHERE id IS NULL AND user_id IS NOT NULL;

-- Drop userId column
ALTER TABLE students DROP COLUMN user_id;

-- Update instructors table
-- First, ensure id column is not null and unique
ALTER TABLE instructors ALTER COLUMN id SET NOT NULL;
ALTER TABLE instructors ADD CONSTRAINT instructors_id_unique UNIQUE (id);

-- Copy data from userId to id where id is null
UPDATE instructors 
SET id = user_id 
WHERE id IS NULL AND user_id IS NOT NULL;

-- Drop userId column
ALTER TABLE instructors DROP COLUMN user_id;

-- Add foreign key constraints if needed
ALTER TABLE practice_students 
ADD CONSTRAINT fk_practice_students_student 
FOREIGN KEY (student_id) REFERENCES students(id);

ALTER TABLE practice_instructors 
ADD CONSTRAINT fk_practice_instructors_instructor 
FOREIGN KEY (instructor_id) REFERENCES instructors(id); 