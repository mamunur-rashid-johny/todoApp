package com.qubelex.todoapp.ui.dialog

import androidx.lifecycle.ViewModel
import com.qubelex.todoapp.data.NoteDao
import com.qubelex.todoapp.di.ApplicationScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeleteViewModel @Inject constructor(
    private val dao: NoteDao,
    @ApplicationScope private val app:CoroutineScope
) :ViewModel() {
    fun onConDelete()=app.launch {
        dao.deleteCompeted()
    }
}
