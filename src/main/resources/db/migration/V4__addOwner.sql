ALTER TABLE schools
ADD COLUMN owner_id UUID NOT NULL,
ADD CONSTRAINT fk_school_owner
    FOREIGN KEY (owner_id)
    REFERENCES users(id)
    ON DELETE CASCADE;