package com.joao01sb.tasklys.features.notes.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteFilter
import com.joao01sb.tasklys.core.domain.model.NoteStatus
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

    private val _detailUiState = MutableStateFlow(NoteDetailUiState())
    val detailUiState: StateFlow<NoteDetailUiState> = _detailUiState

    private val _uiEvent = MutableSharedFlow<NoteUiEvent>()
    val uiEvent: SharedFlow<NoteUiEvent> = _uiEvent.asSharedFlow()

    var currentScreen by mutableStateOf(NoteScreen.LIST)
        private set

    var currentQuery by mutableStateOf("")
        private set

    var selectedFilter by mutableStateOf(NoteFilter.ALL)
        private set

    init {
        loadAllNotes()
    }

    fun onQueryChange(query: String) {
        currentQuery = query
    }

    fun updateSelectedFilter(filter: NoteFilter) {
        selectedFilter = filter
    }

    fun navigateToDetail(noteId: Long? = null) {
        if (noteId != null) {
            loadNoteById(noteId)
        } else {
            _detailUiState.value = NoteDetailUiState(
                isEditing = true
            )
        }
        currentScreen = NoteScreen.DETAIL
    }

    fun navigateBackToList() {
        _detailUiState.value = NoteDetailUiState()
        currentScreen = NoteScreen.LIST
    }

    fun loadAllNotes() {
        _uiState.value = NoteUiState(isLoading = true)
        viewModelScope.launch {
            allNotes.invoke()
                .catch {
                    _uiState.value = NoteUiState(
                        error = it.message ?: "Unknown error",
                        isLoading = false
                    )
                }.collect {
                    if (it.isEmpty()) {
                        _uiState.value = NoteUiState(
                            error = "No notes found",
                            isLoading = false
                        )
                    } else {
                        _uiState.value = NoteUiState(
                            data = it,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun createNote() {
        val note = Note(
            title = _detailUiState.value.title,
            content = _detailUiState.value.content,
            expiresAt = _detailUiState.value.expiryDate
        )

        viewModelScope.launch {
            addNote.invoke(note)
                .onSuccess {
                    _uiEvent.emit(NoteUiEvent.Success("Note created"))
                    navigateBackToList()
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun saveNote() {
        val note = _detailUiState.value.note.copy(
            title = _detailUiState.value.title,
            content = _detailUiState.value.content,
            expiresAt = _detailUiState.value.expiryDate
        )

        viewModelScope.launch {
            updateNote.invoke(note)
                .onSuccess {
                    _uiEvent.emit(NoteUiEvent.Success("Note updated"))
                    toggleEditing(false)
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun handleDeleteAllNotes() {
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

    fun handleDeleteNote() {
        viewModelScope.launch {
            deleteNote.invoke(_detailUiState.value.note)
                .onSuccess {
                    _uiEvent.emit(NoteUiEvent.Success("Note deleted"))
                    navigateBackToList()
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun handleDeleteNoteFromList(note: Note) {
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

    private fun loadNoteById(id: Long) {
        _detailUiState.value = NoteDetailUiState(isLoading = true)
        viewModelScope.launch {
            getNoteById.invoke(id)
                .onSuccess { note ->
                    if (note == null) {
                        _detailUiState.value = NoteDetailUiState(
                            error = "Note not found"
                        )
                    } else {
                        _detailUiState.value = NoteDetailUiState(
                            note = note,
                            title = note.title,
                            content = note.content,
                            expiryDate = note.expiresAt,
                            isEditing = false
                        )
                    }
                }
                .onFailure {
                    _detailUiState.value = NoteDetailUiState(
                        error = it.message ?: "Unknown error"
                    )
                }
        }
    }

    fun searchNotes(query: String) {
        if (query.isBlank()) {
            loadAllNotes()
            return
        }

        viewModelScope.launch {
            getNotesByFilter.invoke(query)
                .onSuccess {
                    _uiState.value = NoteUiState(
                        data = it,
                        isLoading = false
                    )
                }
                .onFailure {
                    _uiState.value = NoteUiState(
                        error = it.message ?: "Unknown error",
                        isLoading = false
                    )
                }
        }
    }

    fun toggleCompleteNote() {
        val currentNote = _detailUiState.value.note
        val newStatus = if (currentNote.status == NoteStatus.COMPLETED) {
            NoteStatus.ACTIVE
        } else {
            NoteStatus.COMPLETED
        }

        val updatedNote = currentNote.copy(status = newStatus)

        viewModelScope.launch {
            updateNote.invoke(updatedNote)
                .onSuccess {
                    _detailUiState.value = _detailUiState.value.copy(
                        note = updatedNote
                    )
                    _uiEvent.emit(NoteUiEvent.Success("Note status updated"))
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun toggleCompleteNoteFromList(note: Note) {
        val newStatus = if (note.status == NoteStatus.COMPLETED) {
            NoteStatus.ACTIVE
        } else {
            NoteStatus.COMPLETED
        }

        val updatedNote = note.copy(status = newStatus)

        viewModelScope.launch {
            updateNote.invoke(updatedNote)
                .onSuccess {
                    _uiEvent.emit(NoteUiEvent.Success("Note status updated"))
                }
                .onFailure {
                    _uiEvent.emit(NoteUiEvent.Error(it.message ?: "Unknown error"))
                }
        }
    }

    fun updateTitle(title: String) {
        _detailUiState.value = _detailUiState.value.copy(title = title)
    }

    fun updateContent(content: String) {
        _detailUiState.value = _detailUiState.value.copy(content = content)
    }

    fun updateExpiryDate(date: Long?) {
        _detailUiState.value = _detailUiState.value.copy(expiryDate = date)
    }

    fun toggleDatePicker(show: Boolean) {
        _detailUiState.value = _detailUiState.value.copy(showDatePicker = show)
    }

    fun toggleEditing(editing: Boolean) {
        _detailUiState.value = _detailUiState.value.copy(isEditing = editing)
    }
}