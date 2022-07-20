package com.qubelex.todoapp.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.qubelex.todoapp.common.Constant
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Constant.TODO_PREF)
//data class to retrieve settings data
data class SettingData(val sortOrder: SortOrder,val hideCompleted:Boolean)

class SettingsPreference(context: Context) {

    private object PreferencesKeys{
        val SORT_ORDER = stringPreferencesKey("sort_order")
        val HIDE_COMPLETED = booleanPreferencesKey("hide_completed")
    }

    private val dataStore = context.dataStore

    val settingsFlow = dataStore.data
        .catch { exp->
            if (exp is IOException){
                emit(emptyPreferences())
            }else{
                throw exp
            }
        }
        .map { pref->
            val sortOrder =SortOrder.valueOf(
                pref[PreferencesKeys.SORT_ORDER]?:SortOrder.BY_DATE.name
            )
            val hideCompleted = pref[PreferencesKeys.HIDE_COMPLETED]?:false
            SettingData(sortOrder,hideCompleted)
        }


    //save sort order data
    suspend fun saveSortOrder(sortOrder: SortOrder){
        dataStore.edit {pref->
            pref[PreferencesKeys.SORT_ORDER] = sortOrder.name
        }
    }
    //save hide completed data
    suspend fun saveHideCompleted(hideCompleted:Boolean){
        dataStore.edit { pref->
            pref[PreferencesKeys.HIDE_COMPLETED] = hideCompleted
        }
    }


}
