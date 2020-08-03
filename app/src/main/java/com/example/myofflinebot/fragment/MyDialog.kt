package com.example.myofflinebot.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myofflinebot.activity.MainActivity
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.presentation.MainPresenter

class MyDialog(activity: MainActivity, mainPresenter: MainPresenter, message: Message,val title:String,val messageText:String) :
    DialogFragment() {
    private val c = context;
    private val presenter = mainPresenter
    private val mes = message
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        val builder = AlertDialog.Builder(it)
        builder.setTitle(title)
            .setMessage(messageText)
            .setPositiveButton("Да") { _, id ->
                when(title){
                    "Удалить" ->  presenter.delitMessage(activity!!.applicationContext, mes)
                    "Очистить все"->  presenter.delitListMessaga(activity!!.applicationContext)
                    "Выход"-> activity!!.finish()
                }
            }
            .setNegativeButton("Нет") { dialog, id ->
                dialog.dismiss()
            }
            .create()
    } ?: throw IllegalStateException("Activity")
}