package com.qubelex.todoapp.data

import androidx.room.*
import com.qubelex.todoapp.utils.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    //get all notes
    fun getTask(query:String,sortOrder: SortOrder,hideCompleted: Boolean):Flow<List<Note>> =
        when(sortOrder){
            SortOrder.BY_NAME-> getTasksSortedByName(query,hideCompleted)
            SortOrder.BY_DATE->getTasksSortedByDateCreated(query,hideCompleted)
        }

    @Query("SELECT * FROM note_table WHERE (completed != :hideCompleted OR completed = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY important DESC, title")
    fun getTasksSortedByName(searchQuery: String, hideCompleted: Boolean): Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE (completed != :hideCompleted OR completed = 0) AND title LIKE '%' || :searchQuery || '%' ORDER BY important DESC, created")
    fun getTasksSortedByDateCreated(searchQuery: String, hideCompleted: Boolean): Flow<List<Note>>




    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("DELETE FROM note_table WHERE completed = 1")
    suspend fun deleteCompeted()
}
