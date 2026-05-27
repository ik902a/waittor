ALTER TABLE users
    RENAME COLUMN username TO login;

ALTER TABLE users ADD COLUMN email varchar;

ALTER TABLE users ADD COLUMN role varchar CHECK (role IN ('ADMIN', 'CLIENT'));