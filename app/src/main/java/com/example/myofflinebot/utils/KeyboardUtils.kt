package com.example.myofflinebot.utils

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager

object KeyboardUtils {
    fun hide(activity: Activity) {
        val input = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        input.hideSoftInputFromWindow(
            activity.currentFocus?.applicationWindowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}