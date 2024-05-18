ALTER TABLE users RENAME COLUMN role_uuid TO capabilities;
ALTER TABLE users ADD COLUMN role VARCHAR(32);
