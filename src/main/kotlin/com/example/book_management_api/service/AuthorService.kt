package com.example.book_management_api.service

import com.example.book_management_api.dto.request.AuthorCreateRequest
import com.example.book_management_api.dto.request.AuthorUpdateRequest
import com.example.book_management_api.dto.response.AuthorBooksResponse
import com.example.book_management_api.repository.AuthorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional
class AuthorService (
    private val authorRepository: AuthorRepository
) {

    /**
     * 著者情報を登録する。
     */
    fun createAuthor(request: AuthorCreateRequest) {
        if(request.birthDate.isAfter(LocalDate.now())){
            throw IllegalArgumentException("生年月日は現在日以前を入力してください。")
        }
        authorRepository.insert(request)
    }

    /**
     * 著者情報を更新する。
     */
    fun updateAuthor(
        authorId: Long,
        request: AuthorUpdateRequest
    ) {
        if(request.birthDate.isAfter(LocalDate.now())){
            throw IllegalArgumentException("生年月日は現在日以前を入力してください。")
        }
        authorRepository.update(authorId, request)
    }

    /**
     * 著者に紐づく書籍情報を取得する。
     */
    fun getBooksByAuthor(
        authorId: Long
    ): AuthorBooksResponse {
        return authorRepository.findBooksByAuthorId(authorId)
    }
}