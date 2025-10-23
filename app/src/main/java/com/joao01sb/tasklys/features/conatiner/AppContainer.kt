package com.joao01sb.tasklys.features.notes.conatiner

import android.content.Context
import com.joao01sb.tasklys.core.data.local.NoteDatabase
import com.joao01sb.tasklys.core.data.repository.NoteRepositoryImp
import com.joao01sb.tasklys.core.domain.usecase.AddNote
import com.joao01sb.tasklys.core.domain.usecase.AllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteAllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteNote
import com.joao01sb.tasklys.core.domain.usecase.GetNoteById
import com.joao01sb.tasklys.core.domain.usecase.GetNotesByFilter
import com.joao01sb.tasklys.core.domain.usecase.UpdateNote

class AppContainer(context: Context) {
    private val db = NoteDatabase.getDatabase(context)
    private val noteDao = db.noteDao()
    private val repository = NoteRepositoryImp(noteDao)

    val addNote = AddNote(repository)
    val allNotes = AllNotes(repository)
    val deleteAllNotes = DeleteAllNotes(repository)
    val deleteNote = DeleteNote(repository)
    val getNoteById = GetNoteById(repository)
    val updateNote = UpdateNote(repository)
    val getNotesByFilter = GetNotesByFilter(repository)
}