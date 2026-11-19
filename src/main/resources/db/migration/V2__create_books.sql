CREATE TABLE books (
    id        BIGSERIAL PRIMARY KEY,
    title     VARCHAR(120) NOT NULL,
    isbn      VARCHAR(13)     NOT NULL UNIQUE,
    year      INTEGER      NOT NULL CHECK (year BETWEEN 1450 AND 2100),
    copies    INTEGER      NOT NULL CHECK (copies >= 0),
    author_id BIGINT       NOT NULL REFERENCES authors (id)
);
CREATE INDEX ix_books_author ON books (author_id);
