package com.joao01sb.tasklys.core.data.mock

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteStatus

object MockData {
    private val currentTime = System.currentTimeMillis()
    
    val mockNotes = listOf(
        Note(
            id = 1L,
            title = "Reunião com cliente",
            content = "Discutir requisitos do novo projeto e definir cronograma de entregas para o próximo trimestre. Preparar apresentação com mockups.",
            createdAt = currentTime - (2 * 24 * 60 * 60 * 1000),
            expiresAt = currentTime + (3 * 24 * 60 * 60 * 1000),
            status = NoteStatus.ACTIVE
        ),
        
        Note(
            id = 2L,
            title = "Estudar Compose",
            content = "Revisar conceitos de State, LazyColumn e Navigation. Praticar com projetos pessoais.",
            createdAt = currentTime - (1 * 24 * 60 * 60 * 1000),
            expiresAt = null,
            status = NoteStatus.ACTIVE
        ),
        
        Note(
            id = 3L,
            title = "Comprar mantimentos",
            content = "Leite, pão, ovos, frutas e verduras para a semana. Não esquecer do café!",
            createdAt = currentTime - (5 * 24 * 60 * 60 * 1000),
            expiresAt = currentTime - (1 * 24 * 60 * 60 * 1000),
            status = NoteStatus.ACTIVE
        ),
        
        Note(
            id = 4L,
            title = "Exercícios físicos",
            content = "30 minutos de caminhada no parque e alongamentos",
            createdAt = currentTime - (3 * 24 * 60 * 60 * 1000),
            expiresAt = null,
            status = NoteStatus.COMPLETED
        ),
        
        Note(
            id = 5L,
            title = "Ligar para dentista",
            content = "",
            createdAt = currentTime - (6 * 60 * 60 * 1000),
            expiresAt = currentTime + (2 * 60 * 60 * 1000),
            status = NoteStatus.ACTIVE
        ),
        
        Note(
            id = 6L,
            title = "Plano de estudos",
            content = "Criar cronograma detalhado para estudar Android Development: Kotlin fundamentals, Compose UI, Architecture Components, Testing, Clean Architecture, MVVM, Repository Pattern, Room Database, Retrofit, Coroutines e Flow. Definir metas semanais e projetos práticos.",
            createdAt = currentTime - (7 * 24 * 60 * 60 * 1000),
            expiresAt = currentTime + (30 * 24 * 60 * 60 * 1000),
            status = NoteStatus.ACTIVE
        )
    )
    
    val singleActiveNote = mockNotes[0]
    val singleExpiredNote = mockNotes[2]
    val singleCompletedNote = mockNotes[3]
    val emptyNote = Note(
        id = 0L,
        title = "",
        content = "",
        createdAt = currentTime,
        expiresAt = null,
        status = NoteStatus.ACTIVE
    )
}