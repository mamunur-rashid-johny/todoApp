package com.qubelex.todoapp.ui.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.qubelex.todoapp.data.Note
import com.qubelex.todoapp.data.NoteDao
import com.qubelex.todoapp.utils.SettingsPreference
import com.qubelex.todoapp.utils.SortOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val dao: NoteDao,
    private val settingsPreference: SettingsPreference
) : ViewModel() {

    val searchQuery = MutableStateFlow("")
    val settingFlow = settingsPreference.settingsFlow


    @OptIn(ExperimentalCoroutinesApi::class)
    private val taskFlow = combine(
        searchQuery,
        settingFlow
    ) { query,settingFlow ->
        Pair(query,settingFlow)
    }.flatMapLatest {(query,settingFlow)->
        dao.getTask(query,settingFlow.sortOrder,settingFlow.hideCompleted)
    }

    val tasks = taskFlow.asLiveData()



    //for update setting pref manager
    fun updateSortOrder(sortOrder: SortOrder){
        viewModelScope.launch {
            settingsPreference.saveSortOrder(sortOrder)
        }
    }

    fun updateHideCompleted(hideCompleted: Boolean){
        viewModelScope.launch {
            settingsPreference.saveHideCompleted(hideCompleted)
        }
    }

    fun onNoteSelected(note: Note){}
    fun onNoteChecked(note: Note,isChecked:Boolean){}


}
