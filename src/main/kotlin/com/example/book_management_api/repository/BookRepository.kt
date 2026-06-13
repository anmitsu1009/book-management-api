package com.example.book_management_api.repository

import com.example.book_management_api.dto.request.BookCreateRequest
import com.example.book_management_api.dto.request.BookUpdateRequest
import com.example.book_management_api.enums.PublishStatus
import com.example.book_management_api.jooq.tables.BookAuthors.BOOK_AUTHORS
import com.example.book_management_api.jooq.tables.Books.BOOKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
class BookRepository(
    private val dsl: DSLContext
) {

    /**
     * 書籍情報を登録する。
     */
    fun insert(request: BookCreateRequest) {
        val bookId = dsl.insertInto(BOOKS)
            .set(BOOKS.TITLE, request.title)
            .set(BOOKS.PRICE, request.price)
            .set(BOOKS.PUBLISH_STATUS, request.publishStatus.label)
            .returning(BOOKS.BOOK_ID)
            .fetchOne()
            ?.get(BOOKS.BOOK_ID)
            ?: throw IllegalStateException("書籍登録に失敗しました")

        request.authorIds.forEach { authorId ->

            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookId)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                .execute()
        }
    }

    /**
     * 書籍の出版状況を取得する。
     *
     * 出版済みステータスから未出版ステータスに更新できないようServiceでチェックするため。
     */
    fun findPublishStatus(
        bookId: Long
    ): PublishStatus? {

        return dsl.select(BOOKS.PUBLISH_STATUS)
            .from(BOOKS)
            .where(BOOKS.BOOK_ID.eq(bookId))
            .fetchOne(BOOKS.PUBLISH_STATUS)
            ?.let(PublishStatus::fromLabel)
    }

    /**
     * 書籍情報を更新する。
     *
     * 書籍情報更新後、著者との関連情報も更新するため、
     * book_authorsテーブルの既存データを削除して再登録する。
     */
    @Transactional
    fun update(
        bookId: Long,
        request: BookUpdateRequest
    ) {
        val count = dsl.update(BOOKS)
            .set(BOOKS.TITLE, request.title)
            .set(BOOKS.PRICE, request.price)
            .set(BOOKS.PUBLISH_STATUS, request.publishStatus.label)
            .where(BOOKS.BOOK_ID.eq(bookId))
            .execute()

        // 指定された書籍が存在しない場合は例外を送出する。
        if (count == 0) {
            throw IllegalArgumentException("書籍が存在しません。")
        }

        // 著者との関連情報を再作成するため既存データを削除する。
        dsl.deleteFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(bookId))
            .execute()

        request.authorIds.forEach { authorId ->
            dsl.insertInto(BOOK_AUTHORS)
                .set(BOOK_AUTHORS.BOOK_ID, bookId)
                .set(BOOK_AUTHORS.AUTHOR_ID, authorId)
                .execute()
        }
    }
}