package com.example.myofflinebot.fragment

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.presentation.MainPresenter

class MyDialog(context: Context, mainPresenter: MainPresenter, message: Message) :
    DialogFragment() {
    val c = context;
    val presenter = mainPresenter
    val mes = message
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Удалить сообщение")
                .setMessage("Вы точно хотите удалить это сообщение?")
                .setPositiveButton("Да") { dialog, id ->
                    presenter.delitMessage(c, mes)
                }
                .setNegativeButton("Нет") { dialog, id ->
                    dialog.dismiss()
                }
                .create()
        } ?: throw IllegalStateException("Activity")
    }
}