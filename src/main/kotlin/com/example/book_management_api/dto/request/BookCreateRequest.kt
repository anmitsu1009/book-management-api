package com.example.book_management_api.dto.request

import com.example.book_management_api.enums.PublishStatus

data class BookCreateRequest(
    val title: String, // タイトル
    val price: Int, // 価格
    val publishStatus: PublishStatus, // 出版状況
    val authorIds: List<Long> // 著者ID
)