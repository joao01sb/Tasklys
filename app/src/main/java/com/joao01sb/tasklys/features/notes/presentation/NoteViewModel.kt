package com.joao01sb.tasklys.features.notes.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.usecase.AddNote
import com.joao01sb.tasklys.core.domain.usecase.AllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteAllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteNote
import com.joao01sb.tasklys.core.domain.usecase.GetNoteById
import com.joao01sb.tasklys.core.domain.usecase.GetNotesByFilter
import com.joao01sb.tasklys.core.domain.usecase.UpdateNote
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class NoteViewModel(
    private val addNote: AddNote,
    private val allNotes: AllNotes,
    private val deleteAllNotes: DeleteAllNotes,
    private val deleteNote: DeleteNote,
    private val getNoteById: GetNoteById,
    private val updateNote: UpdateNote,
    private val getNotesByFilter: GetNotesByFilter
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteUiState<List<Note>>())
    val uiState: StateFlow<NoteUiState<List<Note>>> = _uiState

    private val _uiEvent = MutableSharedFlow<NoteUiEvent>()
    val uiEvent: SharedFlow<NoteUiEvent> = _uiEvent.asSharedFlow()

    private lateinit var currentNote: Note

    init {
        allNotes()
    }

    fun allNotes() {
        _uiState.value = NoteUiState(isLoading = true)
        viewModelScope.launch {
            allNotes.invoke()
                .catch {
                    _uiState.value = NoteUiState(error = it.message ?: "Unknown error", isLoading = false)
                }.collect {
                    if (it.isEmpty()) {
                        _uiState.value = NoteUiState(error = "No notes found", isLoading = false)
                    } else {
                        _uiState.value = NoteUiState(data = it, isLoading = false)
                    }
                }
        }
    }

    fun addNote(note: Note) {
        viewModelScope.launch {
            addNote.invoke(note)
                .onSuccess {

                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun deleteAllNotes() {
        viewModelScope.launch {
            deleteAllNotes.invoke()
                .onSuccess {
                    _uiEvent.emit(NoteUiEvent.Success("All notes deleted"))
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            deleteNote.invoke(note)
                .onSuccess {
                    _uiEvent.emit(NoteUiEvent.Success("Note deleted"))
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun getNoteById(id: Long) {
        viewModelScope.launch {
            getNoteById.invoke(id)
                .onSuccess {
                    if (it == null) {
                        _uiEvent.emit(NoteUiEvent.Error("Note not found"))
                    } else {
                        currentNote = it
                    }
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun getNotesByFilter(query: String) {
        viewModelScope.launch {
            getNotesByFilter.invoke(query)
                .onSuccess {
                    _uiState.value = NoteUiState(data = it, isLoading = false)
                }
                .onFailure {
                    _uiState.value = NoteUiState(error = it.message ?: "Unknown error", isLoading = false)
                }
        }
    }

    fun updateNote(note: Note) {
        viewModelScope.launch {
            updateNote.invoke(note)
                .onSuccess {
                    _uiEvent.emit(NoteUiEvent.Success("Note updated"))
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

}