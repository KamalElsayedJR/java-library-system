-- Session 12: members can log in. Every seeded account has the password "secret123" (BCrypt, cost 10).
-- Never store a plain password. The hash below is safe to commit because the password is a public demo value.
ALTER TABLE members
    ADD COLUMN password_hash VARCHAR(100),
    ADD COLUMN role          VARCHAR(20) NOT NULL DEFAULT 'MEMBER' CHECK (role IN ('MEMBER', 'LIBRARIAN'));

UPDATE members SET password_hash = '$2a$10$9Hz/YZf0rkqQsQGARvKI4ORJW7MVQfGot3ah2sK0clywhYI.W7Sty';

ALTER TABLE members ALTER COLUMN password_hash SET NOT NULL;

INSERT INTO members (name, email, password_hash, role)
VALUES ('Mona Librarian', 'librarian@example.com', '$2a$10$9Hz/YZf0rkqQsQGARvKI4ORJW7MVQfGot3ah2sK0clywhYI.W7Sty', 'LIBRARIAN');
