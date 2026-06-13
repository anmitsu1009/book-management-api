package com.example.book_management_api.dto.response

import com.example.book_management_api.enums.PublishStatus

data class BookResponse(
    val bookId: Long, // 書籍ID
    val title: String, // タイトル
    val price: Int, // 価格
    val publishStatus: PublishStatus // 出版状況
)