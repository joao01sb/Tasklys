package com.joao01sb.tasklys.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val content: String,
    val createdAt: Long,
    val expiresAt: Long?,
    val status: String
)

