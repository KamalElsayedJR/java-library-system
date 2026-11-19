CREATE TABLE members (
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    email      VARCHAR(120) NOT NULL UNIQUE,
    joined_at  TIMESTAMPTZ  NOT NULL DEFAULT now()
);
