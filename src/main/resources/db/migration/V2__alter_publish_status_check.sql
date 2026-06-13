ALTER TABLE books
DROP CONSTRAINT books_publish_status_check;

ALTER TABLE books
    ADD CONSTRAINT books_publish_status_check
        CHECK (publish_status IN ('未出版', '出版済み'));