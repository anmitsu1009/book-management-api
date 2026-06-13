package com.example.book_management_api.controller

import com.example.book_management_api.dto.request.BookCreateRequest
import com.example.book_management_api.dto.request.BookUpdateRequest
import com.example.book_management_api.enums.PublishStatus
import com.example.book_management_api.service.BookService
import tools.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.nio.charset.StandardCharsets

/**
 * BookControllerの単体テスト
 *
 * 単体テスト仕様書:
 * docs/unit-test-specification.md
 */
@WebMvcTest(BookController::class)
class BookControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var bookService: BookService

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

        doNothing().whenever(bookService).createBook(any())

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = StandardCharsets.UTF_8.name()
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
            }

        verify(bookService).createBook(any())
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

        doNothing().whenever(bookService).updateBook(any(), any())

        mockMvc.put("/books/1") {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = StandardCharsets.UTF_8.name()
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
            }

        verify(bookService).updateBook(1L, request)
    }

    /**
     * 単体テスト B-18
     * 更新時価格が負数（異常）
     */
    @Test
    fun testCreateBookNegativePrice() {

        val request = BookCreateRequest(
            title = "JUnit入門",
            price = -1,
            publishStatus = PublishStatus.PUBLISHED,
            authorIds = listOf(1L)
        )

        whenever(bookService.createBook(any())).thenThrow(
            IllegalArgumentException("価格は0以上を入力してください。")
        )

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = StandardCharsets.UTF_8.name()
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isBadRequest() }
                jsonPath("$.message") { value("価格は0以上を入力してください。") }
            }
    }
}