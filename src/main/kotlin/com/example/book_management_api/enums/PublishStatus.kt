package com.example.book_management_api.enums

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

/**
 * 書籍の出版状態を表すEnum。
 *
 * APIでは「出版済み」「未出版」を受け付け、
 * アプリケーション内部ではEnumとして扱う。
 */
enum class PublishStatus(
    @get:JsonValue
    val label: String
) {
    PUBLISHED("出版済み"),
    UNPUBLISHED("未出版");

    /**
     * リクエストで受け取った表示値からEnumへ変換する。
     *
     * "出版済み" → PUBLISHED
     * "未出版" → UNPUBLISHED
     */
    companion object {
        @JvmStatic
        @JsonCreator
        fun fromLabel(label: String): PublishStatus =
            entries.first { it.label == label }
    }
}