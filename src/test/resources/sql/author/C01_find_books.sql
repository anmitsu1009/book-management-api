INSERT INTO authors(author_id, name, birth_date)
VALUES
    (1, '甘井千代子', '1990-01-01');

INSERT INTO books(book_id, title, price, publish_status)
VALUES
    (1, 'チョコレートの歴史', 1000, '出版済み');

INSERT INTO book_authors(book_id, author_id)
VALUES
    (1, 1);