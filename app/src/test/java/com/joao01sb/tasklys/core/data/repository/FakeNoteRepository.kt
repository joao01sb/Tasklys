package com.joao01sb.tasklys.core.data.repository

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeNoteRepository : NoteRepository {

    private val notesFlow = MutableStateFlow<List<Note>>(emptyList())

    override suspend fun addNote(note: Note): Long {
        val list = notesFlow.value.toMutableList()
        list.add(note)
        notesFlow.value = list
        return note.id
    }

    override suspend fun updateNote(note: Note) {
        val list = notesFlow.value.toMutableList()
        list[list.indexOfFirst { it.id == note.id }] = note
        notesFlow.value = list
    }

    override suspend fun deleteNote(note: Note) {
        val updated = notesFlow.value.toMutableList()
        updated.removeIf { it.id == note.id }
        notesFlow.value = updated
    }

    override suspend fun getNoteById(id: Long): Note? {
        return notesFlow.value.firstOrNull { it.id == id }
    }

    override suspend fun deleteAllNotes() {
        notesFlow.value = emptyList()
    }

    override fun getNotes(): Flow<List<Note>> = notesFlow.map { it.toList() }
}