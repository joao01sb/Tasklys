package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow

class AllNotes(
    private val repository: NoteRepository
) {

    operator fun invoke() : Flow<List<Note>> = repository.getNotes()

}