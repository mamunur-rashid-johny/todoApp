package com.qubelex.todoapp.ui.add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.qubelex.todoapp.data.Note
import com.qubelex.todoapp.data.NoteDao
import com.qubelex.todoapp.ui.ADD_NOTE
import com.qubelex.todoapp.ui.EDIT_NOTE
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val dao: NoteDao,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {


    val note = savedStateHandle.get<Note>("note")
    var taskTitle = savedStateHandle.get<String>("taskName") ?: note?.title ?: ""
        set(value) {
            field = value
            savedStateHandle["taskName"] = value
        }
    var taskImportance = savedStateHandle.get<Boolean>("taskImportance") ?: note?.important ?: false
        set(value) {
            field = value
            savedStateHandle["taskImportance"] = value
        }
    var taskDes = savedStateHandle.get<String>("taskDes") ?: note?.description ?: ""
        set(value) {
            field = value
            savedStateHandle["taskDes"] = value
        }

    private val addEditChannel = Channel<AddEditEvent>()
    val addEditEvent = addEditChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskTitle.isEmpty()) {
            showInvalidMsg("Title can't be empty!")
            return
        }
        if (note != null) {
            val updatedNote =
                note.copy(title = taskTitle, description = taskDes, important = taskImportance)
            updateNote(updatedNote)
        } else {
            val note = Note(taskTitle, taskDes, taskImportance)
            createNote(note)
        }

    }

    private fun showInvalidMsg(s: String) {
        viewModelScope.launch {
            addEditChannel.send(AddEditEvent.ShowInvalidInputMsgTitle(s))
        }
    }

    private fun createNote(note: Note) {
        viewModelScope.launch {
            dao.insert(note)
            addEditChannel.send(AddEditEvent.NavigateWithResult(ADD_NOTE))
        }
    }

    private fun updateNote(note: Note) {
        viewModelScope.launch {
            dao.update(note)
            addEditChannel.send(AddEditEvent.NavigateWithResult(EDIT_NOTE))
        }
    }

    sealed class AddEditEvent{
        data class ShowInvalidInputMsgTitle(val msg:String):AddEditEvent()
        data class NavigateWithResult(val result:Int):AddEditEvent()
    }

}
