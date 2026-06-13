package com.example.book_management_api.service

import com.example.book_management_api.dto.request.BookCreateRequest
import com.example.book_management_api.dto.request.BookUpdateRequest
import com.example.book_management_api.enums.PublishStatus
import com.example.book_management_api.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookService (
    private val bookRepository: BookRepository
) {

    /**
     * 書籍情報を登録する。
     */
    fun createBook(request: BookCreateRequest) {
        if(request.price < 0){
            throw IllegalArgumentException("価格は0以上を入力してください。")
        }
        bookRepository.insert(request)
    }

    /**
     * 書籍情報を更新する。
     */
    fun updateBook(
        bookId: Long,
        request: BookUpdateRequest
    ) {
        if(request.price < 0){
            throw IllegalArgumentException("価格は0以上を入力してください。")
        }

        val currentStatus =
            bookRepository.findPublishStatus(bookId)
                ?: throw IllegalArgumentException("書籍が存在しません。")

        if (
            currentStatus == PublishStatus.PUBLISHED &&
            request.publishStatus == PublishStatus.UNPUBLISHED
        ) {
            throw IllegalArgumentException("出版済みの書籍を未出版へ変更できません。")
        }
        bookRepository.update(bookId, request)
    }
}