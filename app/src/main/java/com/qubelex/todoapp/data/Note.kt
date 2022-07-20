package com.qubelex.todoapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "note_table")
@Parcelize
data class Note(
    val title:String,
    val description:String,
    val isCompleted:Boolean = false,
    val important:Boolean=false,
    val created:Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id:Int = 0
):Parcelable{
    val formattedDate:String
        get() = DateFormat.getDateTimeInstance().format(created)
}
