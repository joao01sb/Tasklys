package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.domain.repository.NoteRepository

class DeleteAllNotes(
    private val repository: NoteRepository
) {

    suspend operator fun invoke() : Result<Unit> {
        return try {
            repository.deleteAllNotes()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}