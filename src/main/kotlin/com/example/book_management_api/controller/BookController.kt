package com.example.book_management_api.controller

import com.example.book_management_api.dto.request.BookCreateRequest
import com.example.book_management_api.dto.request.BookUpdateRequest
import com.example.book_management_api.service.BookService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/books")
class BookController(
    private val bookService: BookService
) {

    /**
     * 書籍情報を登録する。
     */
    @PostMapping
    fun createBook(
        @RequestBody request: BookCreateRequest
    ) {
        bookService.createBook(request)
    }

    /**
     * 書籍情報を更新する。
     */
    @PutMapping("/{bookId}")
    fun updateBook(
        @PathVariable bookId: Long,
        @RequestBody request: BookUpdateRequest
    ) {
        bookService.updateBook(bookId, request)
    }
}