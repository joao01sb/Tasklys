package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.data.repository.FakeNoteRepository
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UpdateNoteTest {

    private lateinit var repository: NoteRepository
    private lateinit var updateNote: UpdateNote

    @Before
    fun setup() {
        repository = FakeNoteRepository()
        updateNote = UpdateNote(repository)
    }

    @Test
    fun `updateNote should modify existing note in repository`() = runTest {
        val originalNote = Note(id = 1, title = "Original Title", content = "Original Content")
        val updatedNote = Note(id = 1, title = "Updated Title", content = "Updated Content")
        repository.addNote(originalNote)

        val initialNotes = repository.getNotes().first()
        assertEquals(1, initialNotes.size)
        assertEquals(originalNote, initialNotes[0])

        updateNote(updatedNote)

        val notesAfterUpdate = repository.getNotes().first()
        assertEquals(1, notesAfterUpdate.size)
        assertEquals(updatedNote, notesAfterUpdate[0])
        assertEquals("Updated Title", notesAfterUpdate[0].title)
        assertEquals("Updated Content", notesAfterUpdate[0].content)
        assertFalse(originalNote in notesAfterUpdate)
    }

    @Test
    fun `updateNote should preserve other notes when updating one`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")
        val note3 = Note(id = 3, title = "Note 3", content = "Content 3")
        repository.addNote(note1)
        repository.addNote(note2)
        repository.addNote(note3)

        val updatedNote2 = Note(id = 2, title = "Updated Note 2", content = "Updated Content 2")
        updateNote(updatedNote2)

        val notes = repository.getNotes().first()
        assertEquals(3, notes.size)
        assertTrue(note1 in notes)
        assertTrue(note3 in notes)
        assertTrue(updatedNote2 in notes)
        assertFalse(note2 in notes)
    }

}