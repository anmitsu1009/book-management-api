package com.example.book_management_api.repository

import com.example.book_management_api.dto.request.BookCreateRequest
import com.example.book_management_api.dto.request.BookUpdateRequest
import com.example.book_management_api.enums.PublishStatus
import org.jooq.DSLContext
import com.example.book_management_api.jooq.tables.Books.BOOKS
import com.example.book_management_api.jooq.tables.BookAuthors.BOOK_AUTHORS

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import kotlin.test.Test
import org.springframework.test.context.jdbc.Sql

/**
 * BookRepositoryの単体テスト
 *
 * 前提データ:
 * src/test/resources/data.sql
 *
 * 単体テスト仕様書:
 * docs/unit-test-specification.md
 */
@SpringBootTest
@Transactional
class BookRepositoryTest {

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var dsl: DSLContext

    /**
     * 単体テスト B-01
     * 書籍登録（正常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/book/B01_create_book.sql"
    )
    fun testCreateBook() {

        val request = BookCreateRequest(
            title = "JUnit入門",
            price = 2500,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L)
        )

        // 登録
        bookRepository.insert(request)

        // 登録結果確認
        val book = dsl.selectFrom(BOOKS)
            .where(BOOKS.TITLE.eq("JUnit入門"))
            .fetchOne()

        assertNotNull(book)

        with(book!!) {
            assertEquals("JUnit入門", title)
            assertEquals(2500, price)
            assertEquals(
                PublishStatus.PUBLISHED.label,
                publishStatus
            )
        }
    }

    /**
     * 単体テスト B-02
     * 複数著者登録（正常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/book/B02_create_book_multiple_authors.sql"
    )
    fun testCreateBookWithMultipleAuthors() {

        val request = BookCreateRequest(
            title = "JUnit入門",
            price = 2500,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L, 2L)
        )

        // 登録
        bookRepository.insert(request)

        // 登録された書籍を取得
        val book = dsl.selectFrom(BOOKS)
            .where(BOOKS.TITLE.eq("JUnit入門"))
            .fetchOne()

        assertNotNull(book)

        // 中間テーブル確認
        val bookAuthors = dsl.selectFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(book!!.bookId))
            .fetch()

        assertEquals(2, bookAuthors.size)

        val authorIds = bookAuthors.map { it.authorId }

        assertTrue(authorIds.contains(1L))
        assertTrue(authorIds.contains(2L))
    }

    /**
     * 単体テスト B-09
     * 書籍更新（正常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/book/B08_update_book.sql"
    )
    fun testUpdateBook() {

        val request = BookUpdateRequest(
            title = "更新後タイトル",
            price = 5000,
            publishStatus = PublishStatus.UNPUBLISHED,
            authorIds = listOf(1L)
        )

        // 更新
        bookRepository.update(1L, request)

        // 更新結果確認
        val book = dsl.selectFrom(BOOKS)
            .where(BOOKS.BOOK_ID.eq(1L))
            .fetchOne()

        assertNotNull(book)

        with(book!!) {
            assertEquals("更新後タイトル", title)
            assertEquals(5000, price)
            assertEquals(
                PublishStatus.UNPUBLISHED.label,
                publishStatus
            )
        }
    }

    /**
     * 単体テスト B-14
     * 存在しない書籍更新（異常）
     */
    @Test
    @Sql("/sql/common/cleanup.sql")
    fun testUpdateBookNotFound() {

        val request = BookUpdateRequest(
            title = "更新後タイトル",
            price = 5000,
            publishStatus = PublishStatus.UNPUBLISHED,
            authorIds = listOf(1L)
        )

        val exception = assertThrows<IllegalArgumentException> {
            bookRepository.update(
                999L,
                request
            )
        }

        assertEquals(
            "書籍が存在しません。",
            exception.message
        )
    }

    /**
     * 単体テスト B-15
     * 著者変更（正常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/book/B14_update_book_authors.sql"
    )
    fun testUpdateBookAuthors() {

        val request = BookUpdateRequest(
            title = "チョコレートの歴史",
            price = 1000,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(2L)
        )

        // 更新
        bookRepository.update(1L, request)

        // 中間テーブル確認
        val bookAuthors = dsl.selectFrom(BOOK_AUTHORS)
            .where(BOOK_AUTHORS.BOOK_ID.eq(1L))
            .fetch()

        assertEquals(1, bookAuthors.size)

        assertEquals(
            setOf(2L),
            bookAuthors.map { it.authorId }.toSet()
        )
    }
}