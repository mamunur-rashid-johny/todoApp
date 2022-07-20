package com.qubelex.todoapp.ui.note

import androidx.lifecycle.*
import com.qubelex.todoapp.data.Note
import com.qubelex.todoapp.data.NoteDao
import com.qubelex.todoapp.ui.ADD_NOTE
import com.qubelex.todoapp.ui.EDIT_NOTE
import com.qubelex.todoapp.utils.SettingsPreference
import com.qubelex.todoapp.utils.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val dao: NoteDao,
    private val settingsPreference: SettingsPreference,
    private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val settingFlow = settingsPreference.settingsFlow
    private val noteEventChannel = Channel<NoteEvent>()
    val noteEvent = noteEventChannel.receiveAsFlow()

    private val tasksFlow = combine(
        searchQuery.asFlow(),
        settingFlow
    ) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        dao.getTask(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }
    val tasks = tasksFlow.asLiveData()


    //for update setting pref manager
    fun updateSortOrder(sortOrder: SortOrder) {
        viewModelScope.launch {
            settingsPreference.saveSortOrder(sortOrder)
        }
    }

    fun updateHideCompleted(hideCompleted: Boolean) {
        viewModelScope.launch {
            settingsPreference.saveHideCompleted(hideCompleted)
        }
    }

    //other functionality
    fun onNoteSelected(note: Note) {
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.NavigateToEditScreen(note))
        }
    }

    fun onNoteChecked(note: Note, isChecked: Boolean) {
        viewModelScope.launch {
            dao.update(note.copy(completed = isChecked))
        }
    }

    fun onSwipeNote(note: Note) {
        viewModelScope.launch {
            dao.delete(note)
            noteEventChannel.send(NoteEvent.ShowOnNoteDelete(note))
        }
    }

    fun onUndoDelete(note: Note) {
        viewModelScope.launch {
            dao.insert(note)
        }
    }

    fun onAddNewNote() {
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.NavigateAddNoteScreen)
        }
    }

    fun deleteAllCompleted(){
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.NavigateToDeleteDialog)
        }
    }


    fun onAddEditResult(result: Int) {
        when (result) {
            ADD_NOTE -> {
                showMessage("Note added")
            }
            EDIT_NOTE -> {
                showMessage("Note update")
            }
        }
    }

    private fun showMessage(s: String) {
        viewModelScope.launch {
            noteEventChannel.send(NoteEvent.ShowConMsg(s))
        }
    }

    //class for event handling
    sealed class NoteEvent {
        object NavigateAddNoteScreen : NoteEvent()
        data class NavigateToEditScreen(val note: Note) : NoteEvent()
        data class ShowOnNoteDelete(val note: Note) : NoteEvent()
        data class ShowConMsg(val msg:String):NoteEvent()
        object NavigateToDeleteDialog:NoteEvent()
    }

}
