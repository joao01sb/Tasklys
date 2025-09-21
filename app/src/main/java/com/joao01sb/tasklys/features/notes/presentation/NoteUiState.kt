package com.joao01sb.tasklys.features.notes.presentation

data class NoteUiState<T>(
    val isLoading: Boolean = false,
    val data: T? = null,
    val error: String? = null
)
