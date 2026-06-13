package com.example.book_management_api.controller

import com.example.book_management_api.dto.request.AuthorCreateRequest
import com.example.book_management_api.dto.request.AuthorUpdateRequest
import com.example.book_management_api.dto.response.AuthorBooksResponse
import com.example.book_management_api.service.AuthorService
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
import org.springframework.test.web.servlet.get
import java.time.LocalDate

/**
 * AuthorControllerの単体テスト
 *
 * 単体テスト仕様書:
 * docs/unit-test-specification.md
 */
@WebMvcTest(AuthorController::class)
class AuthorControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockitoBean
    lateinit var authorService: AuthorService

    /**
     * 単体テスト A-01
     * 著者登録（正常）
     */
    @Test
    fun testCreateAuthor() {

        val request = AuthorCreateRequest(
            name = "テスト著者",
            birthDate = LocalDate.of(2000, 1, 1)
        )

        doNothing().whenever(authorService).createAuthor(any())

        mockMvc.post("/authors") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
            }

        verify(authorService).createAuthor(any())
    }

    /**
     * 単体テスト A-05
     * 著者更新（正常）
     */
    @Test
    fun testUpdateAuthor() {

        val request = AuthorUpdateRequest(
            name = "更新後著者",
            birthDate = LocalDate.of(1995, 1, 1)
        )

        doNothing().whenever(authorService).updateAuthor(any(), any())

        mockMvc.put("/authors/1") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }
            .andExpect {
                status { isOk() }
            }

        verify(authorService).updateAuthor(any(), any())
    }

    /**
     * 単体テスト C-01
     * 著者別書籍取得（正常）
     */
    @Test
    fun testGetBooksByAuthor() {

        val response = AuthorBooksResponse(
            authorId = 1L,
            name = "テスト著者",
            birthDate = LocalDate.of(2000, 1, 1),
            books = emptyList()
        )

        whenever(authorService.getBooksByAuthor(any())).thenReturn(response)

        mockMvc.get("/authors/1/books")
            .andExpect {
                status { isOk() }
            }

        verify(authorService).getBooksByAuthor(any())
    }
}