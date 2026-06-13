INSERT INTO authors(author_id, name, birth_date)
VALUES
    (2, '小鳥凛', '2000-12-31');

INSERT INTO books(book_id, title, price, publish_status)
VALUES
    (1, 'Kotlin入門', 2000, '出版済み'),
    (2, 'SpringBoot入門', 3000, '未出版');

INSERT INTO book_authors(book_id, author_id)
VALUES
    (1, 2),
    (2, 2);