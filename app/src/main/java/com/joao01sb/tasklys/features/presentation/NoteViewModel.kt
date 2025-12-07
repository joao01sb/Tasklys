package com.joao01sb.tasklys.features.notes.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.joao01sb.tasklys.core.domain.model.DayOfWeek
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteFilter
import com.joao01sb.tasklys.core.domain.model.NoteStatus
import com.joao01sb.tasklys.core.domain.model.RecurrenceType
import com.joao01sb.tasklys.core.domain.usecase.AddNote
import com.joao01sb.tasklys.core.domain.usecase.AllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteAllNotes
import com.joao01sb.tasklys.core.domain.usecase.DeleteNote
import com.joao01sb.tasklys.core.domain.usecase.GetNoteById
import com.joao01sb.tasklys.core.domain.usecase.UpdateNote
import com.joao01sb.tasklys.features.notes.presentation.datail.NoteDetailUiState
import com.joao01sb.tasklys.features.notes.presentation.note.NoteUiState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(
    private val addNote: AddNote,
    private val allNotes: AllNotes,
    private val deleteAllNotes: DeleteAllNotes,
    private val deleteNote: DeleteNote,
    private val getNoteById: GetNoteById,
    private val updateNote: UpdateNote
) : ViewModel() {

    private val _detailUiState = MutableStateFlow(NoteDetailUiState())
    val detailUiState: StateFlow<NoteDetailUiState> = _detailUiState

    private val _uiEvent = MutableSharedFlow<NoteUiEvent>()
    val uiEvent: SharedFlow<NoteUiEvent> = _uiEvent.asSharedFlow()

    var currentScreen by mutableStateOf(NoteScreen.LIST)
        private set

    var currentQuery by mutableStateOf("")
        private set

    private val _selectedFilter = MutableStateFlow(NoteFilter.ALL)
    val selectedFilter: StateFlow<NoteFilter> = _selectedFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<NoteUiState<List<Note>>> = combine(
        allNotes.invoke(),
        selectedFilter,
        searchQuery
    ) { notes, filter, query ->

        val searchFiltered = if (query.isBlank()) {
            notes
        } else {
            notes.filter { note ->
                note.title.contains(query, ignoreCase = true) ||
                        note.content.contains(query, ignoreCase = true)
            }
        }

        val finalFiltered = when (filter) {
            NoteFilter.ALL -> searchFiltered
            NoteFilter.COMPLETED -> searchFiltered.filter { it.status == NoteStatus.COMPLETED }
            NoteFilter.ACTIVE -> searchFiltered.filter { it.status == NoteStatus.ACTIVE }
            NoteFilter.EXPIRED -> searchFiltered.filter { it.status == NoteStatus.EXPIRED }

        }

        NoteUiState(
            data = finalFiltered,
            isLoading = false,
            error = null
        )

    }.catch { error ->
        emit(NoteUiState(
            data = emptyList(),
            isLoading = false,
            error = error.message ?: "Unknown error"
        ))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NoteUiState(isLoading = true)
    )

    fun updateSelectedFilter(filter: NoteFilter) {
        _selectedFilter.value = filter
    }

    fun searchNotes(query: String) {
        _searchQuery.value = query
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

    fun onQueryChange(query: String) {
        currentQuery = query
    }

    fun createNote() {
        val note = Note(
            title = _detailUiState.value.title,
            content = _detailUiState.value.content,
            expiresAt = _detailUiState.value.expiryDate,
            recurrenceType = _detailUiState.value.recurrenceType,
            recurrenceDays = _detailUiState.value.selectedDays
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
            expiresAt = _detailUiState.value.expiryDate,
            recurrenceType = _detailUiState.value.recurrenceType,
            recurrenceDays = _detailUiState.value.selectedDays
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

    fun toggleRecurringExpanded() {
        _detailUiState.value = _detailUiState.value.copy(
            isRecurringExpanded = !_detailUiState.value.isRecurringExpanded
        )
    }

    fun updateRecurrenceType(type: RecurrenceType) {
        val days = when (type) {
            RecurrenceType.DAILY -> DayOfWeek.entries.toSet()
            RecurrenceType.WEEKDAYS -> setOf(
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY
            )
            RecurrenceType.WEEKEND -> setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            RecurrenceType.ONCE -> emptySet()
            RecurrenceType.CUSTOM -> _detailUiState.value.selectedDays
        }

        _detailUiState.value = _detailUiState.value.copy(
            recurrenceType = type,
            selectedDays = days
        )
    }

    fun toggleDay(day: DayOfWeek) {
        val current = _detailUiState.value.selectedDays
        val updated = if (current.contains(day)) {
            current - day
        } else {
            current + day
        }
        _detailUiState.value = _detailUiState.value.copy(selectedDays = updated)
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