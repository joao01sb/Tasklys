package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository

class GetNotesByFilter(
    private val repository: NoteRepository
) {

    suspend operator fun invoke(query: String) : Result<List<Note>?> {
        if (query.isBlank())
            return Result.failure(IllegalArgumentException("The query can't be empty."))
        return try {
            val result = repository.getNotesByFilter(query)
            Result.success(result)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}