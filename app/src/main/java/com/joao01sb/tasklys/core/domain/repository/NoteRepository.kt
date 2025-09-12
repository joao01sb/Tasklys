package com.joao01sb.tasklys.core.domain.repository

import com.joao01sb.tasklys.core.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    suspend fun addNote(note: Note) : Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun getNoteById(id: Long): Note?
    suspend fun deleteAllNotes()
    fun getNotes(): Flow<List<Note>>
}