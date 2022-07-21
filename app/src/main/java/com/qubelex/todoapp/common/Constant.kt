package com.qubelex.todoapp.common

import java.text.SimpleDateFormat
import java.util.*

object Constant {
    const val SPLASH_SCREEN_TIME =4000L
    const val DATABASE_NAME = "notes_db"
    const val TODO_PREF ="todo_pref"



    //date formatter
    fun stringDate(timeMills:Long):String{
        val formatObj = SimpleDateFormat("dd MMM, yyyy")
        val res = Date(timeMills)
        return formatObj.format(res)
    }
}
