package com.example.book_management_api.repository

import com.example.book_management_api.dto.request.AuthorCreateRequest
import com.example.book_management_api.dto.request.AuthorUpdateRequest
import com.example.book_management_api.jooq.tables.Authors.AUTHORS
import com.example.book_management_api.enums.PublishStatus
import org.jooq.DSLContext
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.springframework.test.context.jdbc.Sql

/**
 * AuthorRepositoryの単体テスト
 *
 * 前提データ:
 * src/test/resources/data.sql
 *
 * 単体テスト仕様書:
 * docs/unit-test-specification.md
 */
@SpringBootTest
@Transactional
class AuthorRepositoryTest {

    @Autowired
    lateinit var authorRepository: AuthorRepository

    @Autowired
    lateinit var dsl: DSLContext

    /**
     * 単体テスト A-01
     * 著者登録（正常）
     */
    @Test
    @Sql("/sql/common/cleanup.sql")
    fun testCreateAuthor() {

        val request = AuthorCreateRequest(
            name = "テスト著者",
            birthDate = LocalDate.of(2000, 1, 1)
        )

        // 登録
        authorRepository.insert(request)

        // 登録結果確認
        val author = dsl.selectFrom(AUTHORS)
            .where(AUTHORS.NAME.eq("テスト著者"))
            .fetchOne()

        assertNotNull(author)

        with(author!!) {
            assertEquals("テスト著者", name)
            assertEquals(
                LocalDate.of(2000, 1, 1),
                birthDate
            )
        }
    }

    /**
     * 単体テスト A-05
     * 著者更新（正常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/author/A05_update_author.sql")
    fun testUpdateAuthor() {

        val request = AuthorUpdateRequest(
            name = "苦井千代子",
            birthDate = LocalDate.of(2000, 12, 31)
        )

        // 更新
        authorRepository.update(1L, request)

        // 更新結果確認
        val author = dsl.selectFrom(AUTHORS)
            .where(AUTHORS.AUTHOR_ID.eq(1L))
            .fetchOne()

        assertNotNull(author)

        with(author!!) {
            assertEquals("苦井千代子", name)
            assertEquals(
                LocalDate.of(2000, 12, 31),
                birthDate
            )
        }
    }

    /**
     * 単体テスト A-08
     * 存在しない著者更新（異常）
     */
    @Test
    @Sql("/sql/common/cleanup.sql")
    fun testUpdateAuthorNotFound() {

        val request = AuthorUpdateRequest(
            name = "更新後著者",
            birthDate = LocalDate.of(2000, 12, 31)
        )

        val exception = assertThrows<IllegalArgumentException> {
            authorRepository.update(
                999L,
                request
            )
        }

        assertEquals(
            "著者が存在しません。",
            exception.message
        )
    }

    /**
     * 単体テスト C-01
     * 著者別書籍検索（正常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/author/C01_find_books.sql")
    fun testFindBooksByAuthor() {

        val result = authorRepository.findBooksByAuthorId(1L)

        // 著者情報
        assertEquals(1L, result.authorId)
        assertEquals("甘井千代子", result.name)
        assertEquals(
            LocalDate.of(1990, 1, 1),
            result.birthDate
        )

        // 書籍情報
        assertEquals(1, result.books.size)

        with(result.books[0]) {
            assertEquals(1L, bookId)
            assertEquals("チョコレートの歴史", title)
            assertEquals(1000, price)
            assertEquals(
                PublishStatus.PUBLISHED,
                publishStatus
            )
        }
    }

    /**
     * 単体テスト C-02
     * 著者別書籍検索（複数書籍、正常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/author/C02_find_multiple_books.sql")
    fun testFindMultipleBooksByAuthor() {

        val result = authorRepository.findBooksByAuthorId(2L)

        assertEquals(2L, result.authorId)
        assertEquals("小鳥凛", result.name)

        assertEquals(2, result.books.size)

        val titles =
            result.books.map { it.title }

        assertTrue(
            titles.contains("Kotlin入門")
        )

        assertTrue(
            titles.contains("SpringBoot入門")
        )
    }

    /**
     * 単体テスト C-03
     * 存在しない著者（異常）
     */
    @Test
    @Sql("/sql/common/cleanup.sql")
    fun testFindBooksByNonexistentAuthor() {

        val exception = assertThrows<IllegalArgumentException> {
            authorRepository.findBooksByAuthorId(999L)
        }

        assertEquals(
            "著者に紐づく書籍が存在しません。",
            exception.message
        )
    }

    /**
     * 単体テスト C-04
     * 書籍未登録著者（異常）
     */
    @Test
    @Sql(
        "/sql/common/cleanup.sql",
        "/sql/author/C04_author_without_books.sql")
    fun testFindBooksByAuthorWithoutBooks() {

        val exception = assertThrows<IllegalArgumentException> {
            authorRepository.findBooksByAuthorId(3L)
        }

        assertEquals(
            // 現行実装では著者不存在と書籍未登録を区別していないため、C-03と同一例外を返却する。
            "著者に紐づく書籍が存在しません。",
            exception.message
        )
    }
}