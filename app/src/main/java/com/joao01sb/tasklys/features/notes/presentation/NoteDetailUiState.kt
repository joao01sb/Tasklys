package com.joao01sb.tasklys.features.notes.presentation

import com.joao01sb.tasklys.core.domain.model.Note

data class NoteDetailUiState(
    val note: Note = Note(),
    val title: String = "",
    val content: String = "",
    val expiryDate: Long? = null,
    val isEditing: Boolean = false,
    val showDatePicker: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
