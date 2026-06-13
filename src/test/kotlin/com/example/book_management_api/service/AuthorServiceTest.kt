package com.example.book_management_api.service

import com.example.book_management_api.dto.request.AuthorCreateRequest
import com.example.book_management_api.dto.request.AuthorUpdateRequest
import com.example.book_management_api.dto.response.AuthorBooksResponse
import com.example.book_management_api.repository.AuthorRepository

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

import java.time.LocalDate

/**
 * AuthorServiceの単体テスト
 *
 * 単体テスト仕様書:
 * docs/unit-test-specification.md
 */
class AuthorServiceTest {

    private val authorRepository = mock<AuthorRepository>()

    private val authorService = AuthorService(authorRepository)

    /**
     * 単体テスト A-01
     * 著者登録（正常）
     */
    @Test
    fun testAuthorCreate() {

        val request = AuthorCreateRequest(
            name = "山田太郎",
            birthDate = LocalDate.of(1990, 1, 1)
        )

        // 登録
        authorService.createAuthor(request)

        // Repository呼び出し確認
        verify(authorRepository).insert(request)
    }

    /**
     * 単体テスト A-04
     * 生年月日が未来日（異常）
     */
    @Test
    fun testAfterTodayError() {

        val request = AuthorCreateRequest(
            name = "山田太郎",
            birthDate = LocalDate.now().plusDays(1)
        )

        // 例外確認
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                authorService.createAuthor(request)
            }

        // メッセージ確認
        assertEquals("生年月日は現在日以前を入力してください。", exception.message)

        // Repositoryが呼ばれていないことを確認
        verify(authorRepository, never()).insert(any())
    }

    /**
     * 単体テスト A-05
     * 著者更新（正常）
     */
    @Test
    fun testAuthorUpdate() {

        val request = AuthorUpdateRequest(
            name = "更新後著者",
            birthDate = LocalDate.of(2000, 1, 1)
        )

        // 更新
        authorService.updateAuthor(1L, request)

        // Repository呼び出し確認
        verify(authorRepository).update(1L, request)
    }

    /**
     * 単体テスト A-09
     * 更新時の生年月日が未来日（異常）
     */
    @Test
    fun testUpdateAfterTodayError() {

        val request = AuthorUpdateRequest(
            name = "更新後著者",
            birthDate = LocalDate.now().plusDays(1)
        )

        // 例外確認
        val exception =
            assertThrows(IllegalArgumentException::class.java) {
                authorService.updateAuthor(1L, request)
            }

        // メッセージ確認
        assertEquals("生年月日は現在日以前を入力してください。", exception.message)

        // Repositoryが呼ばれていないことを確認
        verify(authorRepository, never()).update(any(), any())
    }

    /**
     * 単体テスト C-01
     * 著者別書籍検索（正常）
     */
    @Test
    fun testGetBooksByAuthor() {

        val response = mock<AuthorBooksResponse>()

        // Repository戻り値設定
        whenever(authorRepository.findBooksByAuthorId(1L)).thenReturn(response)

        // 検索
        val result = authorService.getBooksByAuthor(1L)

        // 戻り値確認
        assertEquals(response, result)

        // Repository呼び出し確認
        verify(authorRepository).findBooksByAuthorId(1L)
    }
}