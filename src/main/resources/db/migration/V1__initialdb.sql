CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE users (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  first_name TEXT,
  last_name TEXT,
  email TEXT UNIQUE NOT NULL,
  password TEXT NOT NULL,
  role TEXT NOT NULL
);

CREATE TABLE schools (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  school_name VARCHAR(500) NOT NULL,
  school_email TEXT UNIQUE,
  school_addresses TEXT[],
  logo_url TEXT,
  phone_numbers TEXT[],
  description TEXT,
  subTitle TEXT
);

CREATE TABLE opening_hours (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  school_id UUID REFERENCES schools(id) ON DELETE CASCADE,
  day_of_week TEXT NOT NULL CHECK (day_of_week IN
    ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
  open_time TIME NOT NULL,
  close_time TIME NOT NULL
);

--CREATE TABLE locations (
--  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
--  school_id UUID REFERENCES schools(id) ON DELETE CASCADE,
--  name TEXT NOT NULL,
--  address TEXT
--);

CREATE TABLE levels (
  id SERIAL PRIMARY KEY,
  name TEXT NOT NULL
);

INSERT INTO levels (name) VALUES ('Basic'), ('Intermediate'), ('Advanced');

CREATE TABLE lessons (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  school_id UUID REFERENCES schools(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  level_id INT REFERENCES levels(id),
  category TEXT NOT NULL CHECK (category IN
    ('GRAMMAR','VOCAB','PRACTICE','GENERAL')),
  content TEXT,
  created_by UUID REFERENCES users(id)
);


CREATE TABLE units (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  lesson_id UUID REFERENCES lessons(id) ON DELETE CASCADE,
  title TEXT NOT NULL,
  content TEXT NOT NULL
  video_url TEXT,
  created_by UUID REFERENCES users(id)
);


CREATE TABLE class_schedule (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
  school_id UUID REFERENCES schools(id) ON DELETE CASCADE,
  day_of_week TEXT NOT NULL CHECK (day_of_week IN
    ('MONDAY','TUESDAY','WEDNESDAY','THURSDAY','FRIDAY','SATURDAY','SUNDAY')),
  start_time TIME NOT NULL,
  end_time TIME NOT NULL,
  mode TEXT NOT NULL CHECK (mode IN ('ONLINE','ONSITE')),
  location TEXT,
  meeting_url TEXT
);

CREATE TABLE staffs (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  school_id UUID NOT NULL REFERENCES schools(id) ON DELETE CASCADE,

  role TEXT NOT NULL CHECK (role IN ('ADMIN','TEACHER','ASSISTANT')),

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

  UNIQUE (user_id, school_id)
);

CREATE TABLE activities (
  id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),

  school_id UUID NOT NULL REFERENCES schools(id) ON DELETE CASCADE,

  title TEXT,
  description TEXT,

  images TEXT[],

  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);