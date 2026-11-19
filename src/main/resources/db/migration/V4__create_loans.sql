-- TODO 1: create the "loans" table. Flyway runs this file once, in version order (after V3).
-- Columns:
--   id           BIGSERIAL primary key
--   book_id      BIGINT, NOT NULL, foreign key to books(id)
--   member_id    BIGINT, NOT NULL, foreign key to members(id)
--   borrowed_at  TIMESTAMPTZ, NOT NULL, default now()
--   due_date     DATE, NOT NULL
--   returned_at  TIMESTAMPTZ, NULL while the book is still out
-- Extra: an index on member_id, because we count a member's active loans on every borrow.
-- Remember: never edit a migration after it ran. If you made a mistake, run
--   docker compose down -v   (deletes the local data)  and start again.

CREATE TABLE loans (
id          BIGSERIAL PRIMARY KEY,
book_id     BIGINT NOT NULL REFERENCES books(id),
member_id   BIGINT NOT NULL REFERENCES members(id),
borrowed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
due_date    DATE NOT NULL,
returned_at TIMESTAMPTZ
);

CREATE INDEX ix_loans_member ON loans (member_id);
