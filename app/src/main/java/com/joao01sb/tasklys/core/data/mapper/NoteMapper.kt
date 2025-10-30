package com.joao01sb.tasklys.core.data.mapper

import com.joao01sb.tasklys.core.data.local.NoteEntity
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteStatus
import com.joao01sb.tasklys.core.domain.model.RecurrenceType
import com.joao01sb.tasklys.core.utils.MapperUtils

fun NoteEntity.toDomain(): Note {
    return Note(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        expiresAt = expiresAt,
        status = NoteStatus.valueOf(status),
        recurrenceType = try {
            RecurrenceType.valueOf(recurrenceType)
        } catch (e: Exception) {
            RecurrenceType.ONCE
        },
        recurrenceDays = MapperUtils.parseRecurrenceDays(recurrenceDays)
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        content = content,
        createdAt = createdAt,
        expiresAt = expiresAt,
        status = status.name,
        recurrenceType = recurrenceType.name,
        recurrenceDays = MapperUtils.serializeRecurrenceDays(recurrenceDays)
    )
}

