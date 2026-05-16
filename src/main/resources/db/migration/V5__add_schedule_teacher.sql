ALTER TABLE class_schedule
ADD COLUMN teacher_id UUID,
ADD CONSTRAINT fk_class_schedule_teacher
    FOREIGN KEY (teacher_id)
    REFERENCES users(id)
    ON DELETE SET NULL;
