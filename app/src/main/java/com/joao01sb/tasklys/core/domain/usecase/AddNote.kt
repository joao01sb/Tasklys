package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository

class AddNote(
   private val repository: NoteRepository
) {

    suspend operator fun invoke(note: Note) : Result<Long> {
        if (note.title.isBlank())
            return Result.failure(IllegalArgumentException("The title of the note can't be empty."))
        return try {
            val id = repository.addNote(note)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}