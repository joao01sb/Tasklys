package com.joao01sb.tasklys.core.data.repository

import android.content.Context
import android.util.Log
import com.joao01sb.tasklys.core.data.local.NoteDao
import com.joao01sb.tasklys.core.data.mapper.toDomain
import com.joao01sb.tasklys.core.data.mapper.toEntity
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.repository.NoteRepository
import com.joao01sb.tasklys.core.service.TaskScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class NoteRepositoryImp(
    private val noteDao: NoteDao,
    private val context: Context
) : NoteRepository {

    private val scheduler = TaskScheduler(context)

    override suspend fun addNote(note: Note) : Long {
        scheduler.scheduleTaskNotification(note)
        return noteDao.insert(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        scheduler.rescheduleTaskNotification(note)
        noteDao.update(note.toEntity())
    }

    override suspend fun deleteNote(note: Note) {
        scheduler.cancelTaskNotification(note.id)
        noteDao.delete(note.toEntity())
    }

    override suspend fun getNoteById(id: Long): Note? {
      return noteDao.getNoteById(id)?.toDomain()
    }

    override suspend fun deleteAllNotes() {
        val taskIds = noteDao.getAllTaskIds()
        scheduler.cancelAllTaskNotifications(taskIds)
        Log.d("TaskRepository", "All ${taskIds.size} tasks have been deleted")
        noteDao.deleteAll()
    }

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