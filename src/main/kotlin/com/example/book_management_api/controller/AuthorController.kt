package com.example.book_management_api.controller

import com.example.book_management_api.dto.request.AuthorCreateRequest
import com.example.book_management_api.dto.request.AuthorUpdateRequest
import com.example.book_management_api.dto.response.AuthorBooksResponse
import com.example.book_management_api.service.AuthorService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/authors")
class AuthorController(
    private val authorService: AuthorService
) {

    /**
     * 著者情報を登録する。
     */
    @PostMapping
    fun createAuthor(
        @RequestBody request: AuthorCreateRequest
    ) {
        authorService.createAuthor(request)
    }

    /**
     * 著者情報を更新する。
     */
    @PutMapping("/{authorId}")
    fun updateAuthor(
        @PathVariable authorId: Long,
        @RequestBody request: AuthorUpdateRequest
    ) {
        authorService.updateAuthor(authorId, request)
    }

    /**
     * 著者に紐づく書籍情報を取得する。
     */
    @GetMapping("/{authorId}/books")
    fun getBooksByAuthor(
        @PathVariable authorId: Long
    ): AuthorBooksResponse {
        return authorService.getBooksByAuthor(authorId)
    }
}