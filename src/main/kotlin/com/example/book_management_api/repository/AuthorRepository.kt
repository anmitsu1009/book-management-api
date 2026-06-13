package com.example.book_management_api.repository

import com.example.book_management_api.dto.request.AuthorCreateRequest
import com.example.book_management_api.dto.request.AuthorUpdateRequest
import com.example.book_management_api.dto.response.AuthorBooksResponse
import com.example.book_management_api.dto.response.BookResponse
import com.example.book_management_api.enums.PublishStatus
import com.example.book_management_api.jooq.tables.Authors.AUTHORS
import com.example.book_management_api.jooq.tables.BookAuthors.BOOK_AUTHORS
import com.example.book_management_api.jooq.tables.Books.BOOKS
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class AuthorRepository(
    private val dsl: DSLContext
) {

    /**
     * 著者情報を登録する。
     */
    fun insert(request: AuthorCreateRequest) {
        dsl.insertInto(AUTHORS)
            .set(AUTHORS.NAME, request.name)
            .set(AUTHORS.BIRTH_DATE, request.birthDate)
            .execute()
    }

    /**
     * 著者情報を更新する。
     */
    fun update(
        authorId: Long,
        request: AuthorUpdateRequest
    ) {
        val count = dsl.update (AUTHORS)
            .set(AUTHORS.NAME, request.name)
            .set(AUTHORS.BIRTH_DATE, request.birthDate)
            .where(AUTHORS.AUTHOR_ID.eq(authorId))
            .execute()

        // 指定された著者が存在しない場合は例外を送出する。
        if (count == 0) {
            throw IllegalArgumentException("著者が存在しません。")
        }
    }

    /**
     * 著者に紐づく書籍情報を取得する。
     */
    fun findBooksByAuthorId(
        authorId: Long
    ): AuthorBooksResponse {
        // 著者・著者書籍関連・書籍テーブルを結合し、著者に紐づく書籍情報を取得する。
        val authorBookRecords = dsl.select(
            AUTHORS.AUTHOR_ID,
            AUTHORS.NAME,
            AUTHORS.BIRTH_DATE,
            BOOKS.BOOK_ID,
            BOOKS.TITLE,
            BOOKS.PRICE,
            BOOKS.PUBLISH_STATUS
        )
            .from(AUTHORS)
            .join(BOOK_AUTHORS)
            .on(AUTHORS.AUTHOR_ID.eq(BOOK_AUTHORS.AUTHOR_ID))
            .join(BOOKS)
            .on(BOOK_AUTHORS.BOOK_ID.eq(BOOKS.BOOK_ID))
            .where(AUTHORS.AUTHOR_ID.eq(authorId))
            .fetch()

        // 検索結果が0件の場合は例外を送出する。
        if (authorBookRecords.isEmpty()) {
            throw IllegalArgumentException("著者に紐づく書籍が存在しません。")
        }

        // 検索結果は書籍ごとに1レコード返却される。
        // 著者情報は全レコードで同一のため、先頭レコードから取得する。
        val authorRecord = authorBookRecords.first()

        return AuthorBooksResponse(
            authorId = authorRecord.get(AUTHORS.AUTHOR_ID)!!,
            name = authorRecord.get(AUTHORS.NAME)!!,
            birthDate = authorRecord.get(AUTHORS.BIRTH_DATE)!!,
            books = authorBookRecords.map {
                BookResponse(
                    bookId = it.get(BOOKS.BOOK_ID)!!,
                    title = it.get(BOOKS.TITLE)!!,
                    price = it.get(BOOKS.PRICE)!!,
                    publishStatus = PublishStatus.fromLabel(
                        it.get(BOOKS.PUBLISH_STATUS)!!
                    )
                )
            }
        )
    }
}