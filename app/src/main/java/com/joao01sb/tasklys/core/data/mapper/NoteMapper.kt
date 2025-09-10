package com.joao01sb.tasklys.core.data.mapper

import com.joao01sb.tasklys.core.data.local.NoteEntity
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteStatus

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        expiresAt = expiresAt,
        status = NoteStatus.valueOf(status)
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        expiresAt = expiresAt,
        status = status.name
    )
}
