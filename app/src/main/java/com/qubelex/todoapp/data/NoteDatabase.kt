package com.qubelex.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Note::class], version = 2)
abstract class NoteDatabase:RoomDatabase() {
    abstract fun noteDao():NoteDao
}
