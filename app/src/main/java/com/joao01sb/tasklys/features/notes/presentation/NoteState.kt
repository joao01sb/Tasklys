package com.joao01sb.tasklys.features.notes.presentation

sealed class NoteState<out T> {
    data class Success<out T>(val value: T) : NoteState<T>()
    data class Error(val message: String, val throwable: Throwable? = null) : NoteState<Nothing>()
    object Loading : NoteState<Nothing>()
}