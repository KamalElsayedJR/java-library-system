INSERT INTO authors (name) VALUES ('George Orwell'), ('Robert C. Martin'), ('Martin Fowler'), ('Naguib Mahfouz');

INSERT INTO books (title, isbn, year, copies, author_id) VALUES
    ('1984', '9780451524935', 1949, 3, 1),
    ('Animal Farm', '9780451526342', 1945, 1, 1),
    ('Clean Code', '9780132350884', 2008, 2, 2),
    ('Clean Architecture', '9780134494166', 2017, 1, 2),
    ('Refactoring', '9780134757599', 2018, 2, 3),
    ('Palace Walk', '9780385264662', 1956, 1, 4);

INSERT INTO members (name, email) VALUES ('Sara Ali', 'sara@example.com'), ('Omar Hassan', 'omar@example.com');

INSERT INTO loans (book_id, member_id, due_date) VALUES (2, 1, CURRENT_DATE + 14);
