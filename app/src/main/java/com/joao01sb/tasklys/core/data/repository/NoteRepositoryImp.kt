package com.joao01sb.tasklys.core.data.repository

import com.joao01sb.tasklys.core.data.local.NoteDao
import com.joao01sb.tasklys.core.data.mapper.toDomain
import com.joao01sb.tasklys.core.data.mapper.toEntity
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImp(
    private val noteDao: NoteDao
) : NoteRepository {

    override suspend fun addNote(note: Note) = noteDao.insert(note.toEntity())

    override suspend fun updateNote(note: Note) = noteDao.update(note.toEntity())

    override suspend fun deleteNote(note: Note) = noteDao.delete(note.toEntity())

    override suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)?.toDomain()

    override suspend fun deleteAllNotes() = noteDao.deleteAll()

    override fun getNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map { notes ->
            notes.map { it.toDomain() }
        }
    }

    override suspend fun getNotesByFilter(query: String): List<Note>? {
        return noteDao.getNotesByFilter(query)
            ?.map { it.toDomain() }
    }

}