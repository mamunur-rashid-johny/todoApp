package com.qubelex.todoapp.data

import androidx.room.*
import com.qubelex.todoapp.utils.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {
    @Query("SELECT * FROM note_table WHERE(isCompleted !=:hideCompleted OR isCompleted = 0) AND title LIKE '%' || :query || '%s' ORDER BY important DESC,title ")
    fun getNotesSortByName(query: String,hideCompleted:Boolean): Flow<List<Note>>

    @Query("SELECT * FROM note_table WHERE(isCompleted !=:hideCompleted OR isCompleted = 0) AND title LIKE '%' || :query || '%s' ORDER BY important DESC,created ")
    fun getNotesSortByDate(query: String,hideCompleted:Boolean): Flow<List<Note>>


    //get all notes
    fun getTask(query:String,sortOrder: SortOrder,hideCompleted: Boolean):Flow<List<Note>> =
        when(sortOrder){
           SortOrder.BY_NAME-> getNotesSortByName(query,hideCompleted)
           SortOrder.BY_DATE->getNotesSortByDate(query,hideCompleted)
        }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)
}
