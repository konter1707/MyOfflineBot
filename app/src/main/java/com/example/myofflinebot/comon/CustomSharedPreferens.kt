package com.example.myofflinebot.comon

import android.content.Context
import android.content.SharedPreferences


class CustomSharedPreferens(private val flag: Int) {
     fun getValueSP(context: Context):String{
            return if (flag==0){
                context.getSharedPreferences(PREF_NAME, PRIVATE_MODE).getString(PREF_NAME, "/menu").toString()
             }else {
                context.getSharedPreferences(PREF_NAME1, PRIVATE_MODE1).getString(PREF_NAME1,"0").toString()
            }
         }
     fun save(context: Context, text: String) {
        val prefs = if (flag==0)
             context.getSharedPreferences(PREF_NAME, PRIVATE_MODE1) else
             context.getSharedPreferences(PREF_NAME1, PRIVATE_MODE1)
         val KEY_NAME=if (flag==0) PREF_NAME else PREF_NAME1
        val editor: SharedPreferences.Editor = prefs.edit()
        editor.putString(KEY_NAME, text)
        editor.apply()
    }
    companion object{
        const val PRIVATE_MODE: Int = 0
        const val PREF_NAME: String = "name_tag"
        const val PRIVATE_MODE1: Int = 0
        const val PREF_NAME1: String = "tags"
    }
}