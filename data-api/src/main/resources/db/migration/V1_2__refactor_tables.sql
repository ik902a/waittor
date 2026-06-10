ALTER TABLE users
    RENAME COLUMN username TO login;

ALTER TABLE users ADD COLUMN email varchar;

ALTER TABLE users ADD COLUMN role varchar CHECK (role IN ('ADMIN', 'CLIENT'));

CREATE TABLE IF NOT EXISTS refresh_tokens
(
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT,
    expiry_date TIMESTAMP WITH TIME ZONE
);
