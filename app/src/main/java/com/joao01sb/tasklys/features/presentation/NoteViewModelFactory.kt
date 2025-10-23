package com.joao01sb.tasklys.features.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.joao01sb.tasklys.core.domain.usecase.AddNote
import com.joao01sb.tasklys.core.domain.usecase.AllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteAllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteNote
import com.joao01sb.tasklys.core.domain.usecase.GetNoteById
import com.joao01sb.tasklys.core.domain.usecase.GetNotesByFilter
import com.joao01sb.tasklys.core.domain.usecase.UpdateNote

class NoteViewModelFactory(
    private val addNote: AddNote,
    private val allNotes: AllNotes,
    private val deleteAllNotes: DeleteAllNotes,
    private val deleteNote: DeleteNote,
    private val getNoteById: GetNoteById,
    private val updateNote: UpdateNote,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(
                addNote, allNotes, deleteAllNotes,
                deleteNote, getNoteById, updateNote
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}