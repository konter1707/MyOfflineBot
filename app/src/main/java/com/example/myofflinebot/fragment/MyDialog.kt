package com.example.myofflinebot.fragment

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.presentation.MainPresenter

class MyDialog(
    mainPresenter: MainPresenter,
    message: Message,
    private val title: String,
    private val messageText: String
) :
    DialogFragment() {
    private val presenter = mainPresenter
    private val msg = message
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = activity?.let {
        val builder = AlertDialog.Builder(it)
        builder.setTitle(title)
            .setMessage(messageText)
            .setPositiveButton("Да") { _, id ->
                when (title) {
                    "Удалить" -> presenter.deleteMessage(requireActivity().applicationContext, msg)
                    "Очистить все" -> presenter.deleteListMessage(requireActivity().applicationContext)
                    "Выход" -> requireActivity().finish()
                }
            }
            .setNegativeButton("Нет") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    } ?: throw IllegalStateException("Activity")
}