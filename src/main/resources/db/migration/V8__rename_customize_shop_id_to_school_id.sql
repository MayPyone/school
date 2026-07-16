DO $$
BEGIN
  IF EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'schools'
      AND column_name = 'customize_shop_id'
  ) AND NOT EXISTS (
    SELECT 1
    FROM information_schema.columns
    WHERE table_name = 'schools'
      AND column_name = 'customize_school_id'
  ) THEN
    ALTER TABLE schools RENAME COLUMN customize_shop_id TO customize_school_id;
  END IF;
END $$;

DROP INDEX IF EXISTS uk_schools_customize_shop_id;

CREATE UNIQUE INDEX IF NOT EXISTS uk_schools_customize_school_id ON schools (customize_school_id);
