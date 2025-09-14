package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.data.repository.FakeNoteRepository
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DeleteAllNotesTest {

    private lateinit var repository: NoteRepository
    private lateinit var deleteAllNotes: DeleteAllNotes

    @Before
    fun setup() {
        repository = FakeNoteRepository()
        deleteAllNotes = DeleteAllNotes(repository)
    }

    @Test
    fun `deleteAllNotes should remove all notes from repository`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")

        repository.addNote(note1)
        repository.addNote(note2)

        val initialNotes = repository.getNotes().first()
        assertEquals(2, initialNotes.size)

        val result = deleteAllNotes()

        assertTrue(result.isSuccess)
        val notesAfterDelete = repository.getNotes().first()
        assertEquals(0, notesAfterDelete.size)
        assertTrue(notesAfterDelete.isEmpty())
    }

    @Test
    fun `deleteAllNotes should succeed when repository is already empty`() = runTest {
        val initialNotes = repository.getNotes().first()
        assertEquals(0, initialNotes.size)

        val result = deleteAllNotes()

        assertTrue(result.isSuccess)
        val notesAfterDelete = repository.getNotes().first()
        assertEquals(0, notesAfterDelete.size)
    }
}