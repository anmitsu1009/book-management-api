package com.example.book_management_api.dto.response

import java.time.LocalDate

data class AuthorBooksResponse(
    val authorId: Long, // 著者ID
    val name: String, // 名前
    val birthDate: LocalDate, // 生年月日
    val books: List<BookResponse> // 書籍情報
)