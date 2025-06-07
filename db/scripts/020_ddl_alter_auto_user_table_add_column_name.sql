ALTER TABLE auto_user ADD COLUMN name varchar;
UPDATE auto_user SET name = login;
ALTER TABLE auto_user ALTER COLUMN name SET NOT NULL;

