package com.joao01sb.tasklys.core.data.mapper

import com.joao01sb.tasklys.core.data.local.NoteEntity
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteStatus
import org.junit.Test


class NoteMapperTest {

    @Test
    fun `should map Note to NoteEntity correctly`() {
        val note = Note(
            id = 1L,
            title = "Test Note",
            content = "This is a test note.",
            createdAt = 100L,
            expiresAt = 1000L,
            status = NoteStatus.ACTIVE
        )

        val entity = note.toEntity()

        assert(entity.id == note.id)
        assert(entity.title == note.title)
        assert(entity.content == note.content)
        assert(entity.createdAt == note.createdAt)
        assert(entity.expiresAt == note.expiresAt)
        assert(entity.status == note.status.name)
    }

    @Test
    fun `should map NoteEntity to Note correctly`() {
        val entity = NoteEntity(
            id = 1L,
            title = "Test Note",
            content = "This is a test note.",
            createdAt = 100L,
            expiresAt = 1000L,
            status = "ACTIVE"
        )

        val note = entity.toDomain()

        assert(note.id == entity.id)
        assert(note.title == entity.title)
        assert(note.content == entity.content)
        assert(note.createdAt == entity.createdAt)
        assert(note.expiresAt == entity.expiresAt)
        assert(note.status.name == entity.status)
    }

}