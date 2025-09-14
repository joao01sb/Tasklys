package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.data.repository.FakeNoteRepository
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetNoteByIdTest {

    private lateinit var repository: NoteRepository
    private lateinit var getNoteById: GetNoteById

    @Before
    fun setup() {
        repository = FakeNoteRepository()
        getNoteById = GetNoteById(repository)
    }

    @Test
    fun `getNoteById should return correct note when id exists`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")

        repository.addNote(note1)
        repository.addNote(note2)

        val result = getNoteById(1)

        assertTrue(result.isSuccess)
        assertEquals(note1, result.getOrNull())
        assertEquals("Note 1", result.getOrNull()?.title)
        assertEquals("Content 1", result.getOrNull()?.content)
    }

    @Test
    fun `getNoteById should return null when id does not exist`() = runTest {
        val note = Note(id = 1, title = "Note", content = "Content")
        repository.addNote(note)

        val result = getNoteById(99)

        assertTrue(result.isSuccess)
        assertNull(result.getOrNull())
    }
}