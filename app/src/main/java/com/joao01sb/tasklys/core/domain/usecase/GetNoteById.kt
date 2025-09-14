package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository

class GetNoteById(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(id: Long) : Result<Note?> {
       return try {
            val note = repository.getNoteById(id)
            Result.success(note)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}