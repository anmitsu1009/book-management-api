CREATE TABLE authors (
                         author_id BIGSERIAL PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         birth_date DATE NOT NULL
);

CREATE TABLE books (
                       book_id BIGSERIAL PRIMARY KEY,
                       title VARCHAR(255) NOT NULL,
                       price INTEGER NOT NULL CHECK(price >= 0),
                       publish_status VARCHAR(20) NOT NULL
                           CHECK (publish_status IN ('UNPUBLISHED', 'PUBLISHED'))
);

CREATE TABLE book_authors (
                              book_id BIGINT NOT NULL,
                              author_id BIGINT NOT NULL,

                              PRIMARY KEY (book_id, author_id),

                              FOREIGN KEY (book_id)
                                  REFERENCES books(book_id),

                              FOREIGN KEY (author_id)
                                  REFERENCES authors(author_id)
);