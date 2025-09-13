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
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class AddNoteTest {

    private lateinit var repository: NoteRepository
    private lateinit var addNote: AddNote

    @Before
    fun setup() {
        repository = FakeNoteRepository()
        addNote = AddNote(repository)
    }

    @Test
    fun `addNote should insert note into repository`() = runTest {
        val note = Note(title = "Test", content = "content")

        addNote(note)
        val notes = repository.getNotes().first()

        assertTrue(note in notes)
        assertEquals(1, notes.size)
        assertEquals(note, notes[0])
    }

}