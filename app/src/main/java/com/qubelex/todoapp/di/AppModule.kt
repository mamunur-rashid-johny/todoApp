package com.qubelex.todoapp.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.qubelex.todoapp.common.Constant
import com.qubelex.todoapp.data.NoteDatabase
import com.qubelex.todoapp.utils.SettingsPreference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
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

    @Provides
    @Singleton
    fun providesSettingPref(@ApplicationContext context: Context) = SettingsPreference(context = context)

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope
