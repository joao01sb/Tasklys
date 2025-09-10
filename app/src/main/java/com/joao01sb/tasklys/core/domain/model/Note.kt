package com.joao01sb.tasklys.core.domain.model

data class Note(
    val id: Long = 0L,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val expiresAt: Long? = null,
    val status: NoteStatus = NoteStatus.ACTIVE
)

enum class NoteStatus {
    ACTIVE,
    EXPIRED,
    ARCHIVED
}
