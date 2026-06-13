package com.example.book_management_api.dto.request

import java.time.LocalDate

data class AuthorUpdateRequest(
    val name: String, // 名前
    val birthDate: LocalDate // 生年月日
)