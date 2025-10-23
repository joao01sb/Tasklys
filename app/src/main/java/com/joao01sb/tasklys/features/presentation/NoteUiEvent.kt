package com.joao01sb.tasklys.features.notes.presentation

sealed class NoteUiEvent {
    data class Success(val message: String) : NoteUiEvent()
    data class Error(val message: String) : NoteUiEvent()
}