package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.data.repository.FakeNoteRepository
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AllNotesTest {

    private lateinit var repository: NoteRepository
    private lateinit var allNotes: AllNotes

    @Before
    fun setup() {
        repository = FakeNoteRepository()
        allNotes = AllNotes(repository)
    }

    @Test
    fun `allNotes should return all notes from repository`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")
        val note3 = Note(id = 3, title = "Note 3", content = "Content 3")

        repository.addNote(note1)
        repository.addNote(note2)
        repository.addNote(note3)

        val result = allNotes().first()

        assertEquals(3, result.size)
        assertTrue(note1 in result)
        assertTrue(note2 in result)
        assertTrue(note3 in result)
    }

    @Test
    fun `allNotes should return empty list when repository is empty`() = runTest {
        val result = allNotes().first()

        assertEquals(0, result.size)
        assertTrue(result.isEmpty())
    }

}