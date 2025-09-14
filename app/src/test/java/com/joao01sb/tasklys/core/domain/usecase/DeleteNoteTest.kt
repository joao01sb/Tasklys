package com.joao01sb.tasklys.core.domain.usecase

import com.joao01sb.tasklys.core.data.repository.FakeNoteRepository
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class DeleteNoteTest {

    private lateinit var repository: NoteRepository
    private lateinit var deleteNote: DeleteNote

    @Before
    fun setup() {
        repository = FakeNoteRepository()
        deleteNote = DeleteNote(repository)
    }

    @Test
    fun `deleteNote should delete note from repository`() = runTest {
        val note1 = Note(id = 1, title = "Note 1", content = "Content 1")
        val note2 = Note(id = 2, title = "Note 2", content = "Content 2")

        repository.addNote(note1)
        repository.addNote(note2)

        val initialNotes = repository.getNotes().first()
        assertEquals(2, initialNotes.size)

        deleteNote(note1)

        val notesAfterDelete = repository.getNotes().first()
        assertEquals(1, notesAfterDelete.size)
        assertEquals(note2, notesAfterDelete[0])

    }

    @Test
    fun `deleteNote should handle deleting non-existent note`() = runTest {
        val existingNote = Note(id = 1, title = "Existing", content = "Content")
        val nonExistentNote = Note(id = 99, title = "Non-existent", content = "Content")
        repository.addNote(existingNote)

        deleteNote(nonExistentNote)

        val notes = repository.getNotes().first()
        assertEquals(1, notes.size)
        assertTrue(existingNote in notes)
    }


    @Test
    fun `deleteNote should work with empty repository`() = runTest {
        val note = Note(id = 1, title = "Test", content = "Content")

        deleteNote(note)

        val notes = repository.getNotes().first()
        assertEquals(0, notes.size)
    }


}