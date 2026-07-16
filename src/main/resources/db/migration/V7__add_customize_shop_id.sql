ALTER TABLE schools
ADD COLUMN customize_shop_id TEXT;

WITH numbered_schools AS (
  SELECT
    id,
    COALESCE(
      NULLIF(
        REGEXP_REPLACE(
          REGEXP_REPLACE(LOWER(school_name), '[^a-z0-9]+', '-', 'g'),
          '(^-|-$)',
          '',
          'g'
        ),
        ''
      ),
      'school'
    ) AS base_slug,
    ROW_NUMBER() OVER (
      PARTITION BY COALESCE(
        NULLIF(
          REGEXP_REPLACE(
            REGEXP_REPLACE(LOWER(school_name), '[^a-z0-9]+', '-', 'g'),
            '(^-|-$)',
            '',
            'g'
          ),
          ''
        ),
        'school'
      )
      ORDER BY created_at, id
    ) AS duplicate_index
  FROM schools
)
UPDATE schools
SET customize_shop_id = CASE
  WHEN numbered_schools.duplicate_index = 1 THEN numbered_schools.base_slug
  ELSE numbered_schools.base_slug || '-' || (numbered_schools.duplicate_index - 1)
END
FROM numbered_schools
WHERE schools.id = numbered_schools.id;

ALTER TABLE schools
ALTER COLUMN customize_shop_id SET NOT NULL;

CREATE UNIQUE INDEX uk_schools_customize_shop_id ON schools (customize_shop_id);
