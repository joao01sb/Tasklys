package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository

class DeleteNote(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note) : Result<Unit> {
        if (note.id == 0L)
            return Result.failure(IllegalArgumentException("It is not possible to delete a note without an ID"))
        return try {
            repository.deleteNote(note)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}