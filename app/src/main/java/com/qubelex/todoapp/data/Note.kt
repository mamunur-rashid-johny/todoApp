package com.qubelex.todoapp.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.qubelex.todoapp.common.Constant
import kotlinx.parcelize.Parcelize
import java.text.DateFormat

@Entity(tableName = "note_table")
@Parcelize
data class Note(
    val title:String,
    val description:String?=null,
    val completed:Boolean = false,
    val important:Boolean=false,
    val created:Long = System.currentTimeMillis(),
    @PrimaryKey(autoGenerate = true) val id:Int = 0
):Parcelable{
    val formattedDate:String
        get() = Constant.stringDate(created)
      //  get() = DateFormat.getDateTimeInstance().format(created)
}
