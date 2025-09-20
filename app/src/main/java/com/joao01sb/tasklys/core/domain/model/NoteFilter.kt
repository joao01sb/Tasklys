package com.joao01sb.tasklys.core.domain.model

enum class NoteFilter(val displayName: String) {
    ALL("Todas"),
    ACTIVE("Pendentes"),
    COMPLETED("Concluídas"),
    EXPIRED("Vencidas")
}