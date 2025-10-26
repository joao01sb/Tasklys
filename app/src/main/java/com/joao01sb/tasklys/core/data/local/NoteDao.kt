package com.joao01sb.tasklys.core.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY createdAt DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Query("""
        SELECT * FROM notes 
        WHERE expiresAt > :currentTime 
        AND status = 'ACTIVE'
    """)
    suspend fun getTasksToReschedule(currentTime: Long = System.currentTimeMillis()): List<NoteEntity>

    @Query("SELECT id FROM notes")
    suspend fun getAllTaskIds(): List<Long>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: NoteEntity): Long

    @Update
    suspend fun update(note: NoteEntity)

    @Delete
    suspend fun delete(note: NoteEntity)

    @Query("DELETE FROM notes")
    suspend fun deleteAll()

    @Query("UPDATE notes SET status = 'COMPLETED' WHERE id = :taskId")
    suspend fun markAsCompleted(taskId: Long)

    @Query("SELECT * FROM notes WHERE title LIKE '%' || :query || '%' OR content LIKE '%' || :query || '%'")
    suspend fun getNotesByFilter(query: String) : List<NoteEntity>?

}
