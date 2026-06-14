package com.example.book_management_api.service

import com.example.book_management_api.dto.request.BookCreateRequest
import com.example.book_management_api.dto.request.BookUpdateRequest
import com.example.book_management_api.enums.PublishStatus
import com.example.book_management_api.repository.BookRepository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

import org.mockito.kotlin.*

class BookServiceTest {

    private val bookRepository = mock<BookRepository>()

    private val bookService = BookService(bookRepository)

    /**
     * 単体テスト B-01
     * 書籍登録（正常）
     */
    @Test
    fun testCreateBook() {

        val request = BookCreateRequest(
            title = "JUnit入門",
            price = 2500,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L)
        )

        // 登録
        bookService.createBook(request)

        // Repository呼び出し確認
        verify(bookRepository).insert(request)
    }

    /**
     * 単体テスト B-08
     * 価格が負数（異常）
     */
    @Test
    fun testCreateBookNegativePrice() {

        val request = BookCreateRequest(
            title = "JUnit入門",
            price = -1,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L)
        )

        // 例外確認
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                bookService.createBook(request)
            }

        // メッセージ確認
        assertEquals("価格は0以上を入力してください。", exception.message)

        // Repository未実行確認
        verify(bookRepository, never()).insert(any())
    }

    /**
     * 単体テスト B-09
     * 書籍更新（正常）
     */
    @Test
    fun testUpdateBook() {

        val request = BookUpdateRequest(
            title = "更新後タイトル",
            price = 5000,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L)
        )

        whenever(bookRepository.findPublishStatus(1L)).thenReturn(PublishStatus.PUBLISHED)

        // 更新
        bookService.updateBook(1L, request)

        // Repository呼び出し確認
        verify(bookRepository).update(1L, request)
    }

    /**
     * 単体テスト B-14
     * 存在しない書籍更新（異常）
     */
    @Test
    fun testUpdateBookNotFound() {

        val request = BookUpdateRequest(
            title = "更新後タイトル",
            price = 5000,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L)
        )

        whenever(bookRepository.findPublishStatus(999L)).thenReturn(null)

        // 例外確認
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                bookService.updateBook(999L, request)
            }

        // メッセージ確認
        assertEquals("書籍が存在しません。", exception.message)

        // Repository未実行確認
        verify(bookRepository, never()).update(any(), any())
    }

    /**
     * 単体テスト B-17
     * 出版済み→未出版変更（異常）
     */
    @Test
    fun testPublishedToUnpublishedError() {

        val request = BookUpdateRequest(
            title = "更新後タイトル",
            price = 5000,
            publishStatus = PublishStatus.UNPUBLISHED,
            authorIds = listOf(1L)
        )

        whenever(bookRepository.findPublishStatus(1L)).thenReturn(PublishStatus.PUBLISHED)

        // 例外確認
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                bookService.updateBook(1L, request)
            }

        // メッセージ確認
        assertEquals("出版済みの書籍を未出版へ変更できません。", exception.message)

        // Repository未実行確認
        verify(bookRepository, never()).update(any(), any())
    }

    /**
     * 単体テスト B-18
     * 更新時価格が負数（異常）
     */
    @Test
    fun testUpdateBookNegativePrice() {

        val request = BookUpdateRequest(
            title = "更新後タイトル",
            price = -1,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L)
        )

        // 例外確認
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                bookService.updateBook(1L, request)
            }

        // メッセージ確認
        assertEquals("価格は0以上を入力してください。", exception.message)

        // Repository未実行確認
        verify(bookRepository, never()).findPublishStatus(any())

        verify(bookRepository, never()).update(any(), any())
    }
}