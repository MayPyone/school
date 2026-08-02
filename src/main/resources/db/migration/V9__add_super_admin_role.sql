ALTER TABLE staffs
  DROP CONSTRAINT IF EXISTS staffs_role_check;

ALTER TABLE staffs
  ADD CONSTRAINT staffs_role_check
  CHECK (role IN ('SUPER_ADMIN','ADMIN','TEACHER','ASSISTANT'));
