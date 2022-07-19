package com.qubelex.todoapp.di

import android.app.Application
import androidx.room.Room
import com.qubelex.todoapp.common.Constant
import com.qubelex.todoapp.data.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNoteDatabase(app: Application) =
        Room.databaseBuilder(app, NoteDatabase::class.java, Constant.DATABASE_NAME).build()

    @Provides
    @Singleton
    fun providesNoteDao(db: NoteDatabase) = db.noteDao()


}
