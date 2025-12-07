package com.joao01sb.tasklys.features.notes.presentation.datail

import com.joao01sb.tasklys.core.domain.model.DayOfWeek
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.RecurrenceType

data class NoteDetailUiState(
    val note: Note = Note(),
    val title: String = "",
    val content: String = "",
    val expiryDate: Long? = null,
    val isEditing: Boolean = false,
    val showDatePicker: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRecurringExpanded: Boolean = false,
    val recurrenceType: RecurrenceType = RecurrenceType.ONCE,
    val selectedDays: Set<DayOfWeek> = emptySet()
)
