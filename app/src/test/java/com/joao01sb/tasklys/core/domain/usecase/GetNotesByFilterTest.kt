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
class GetNotesByFilterTest {

    private lateinit var repository: NoteRepository
    private lateinit var getNotesByFilter: GetNotesByFilter

    @Before
    fun setup() {
        repository = FakeNoteRepository()
        getNotesByFilter = GetNotesByFilter(repository)
    }

    @Test
    fun `getNotesByFilter should return correct notes when query matches`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")
        val note3 = Note(id = 3, title = "Note 3", content = "Content 3")
        val query = "Note 1"

        repository.addNote(note1)
        repository.addNote(note2)
        repository.addNote(note3)

        val result = getNotesByFilter(query)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals(note1, result.getOrNull()?.first())
    }

    @Test
    fun `getNotesByFilter should return empty list when query does not match`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")
        val query = "Nonexistent"

        repository.addNote(note1)
        repository.addNote(note2)

        val result = getNotesByFilter(query)

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `getNotesByFilter should return error query cannot be empty when the query is empty`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")
        val query = ""

        repository.addNote(note1)
        repository.addNote(note2)

        val result = getNotesByFilter(query)

        assertTrue(result.isFailure)
        assertNull(result.getOrNull())
        assertEquals("The query can't be empty.", result.exceptionOrNull()?.message)
    }

    @Test
    fun `getNotesByFilter should return multiples results when query matches`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 1")
        val note3 = Note(id = 3, title = "Note 3", content = "Content 1")
        val query = "Content 1"

        repository.addNote(note1)
        repository.addNote(note2)
        repository.addNote(note3)

        val result = getNotesByFilter(query)

        assertTrue(result.isSuccess)
        assertEquals(3, result.getOrNull()?.size)
        assertTrue(result.getOrNull()?.containsAll(listOf(note1, note2, note3)) == true)
    }

}