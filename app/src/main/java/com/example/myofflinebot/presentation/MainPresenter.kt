package com.example.myofflinebot.presentation

import android.annotation.SuppressLint
import android.content.Context
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.data.db.MessageDB
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter

@InjectViewState
@SuppressLint("CheckResult")
class MainPresenter : MvpPresenter<MainView>() {
    fun addUserMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao()!!.insertRx(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    viewState.starOnClick("")

                },
                { error ->
                    viewState.onError("" + error)
                })
    }
    fun getMessageBot(context: Context, userText: String) {
        BotJob(context).listBotJob(userText)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.setMessageBot(it)
            }, {
                viewState.setMessageBot("Произошла ошибка. Проверьте написанное!")
            }
            )
    }
    fun addBotMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao()!!.insertRx(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    viewState.starOnClick("")
                },
                { error ->
                    viewState.onError("" + error)
                })
    }

    fun setListener(context: Context, isOut: Boolean) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    viewState.setList(result)
                    if (isOut == false) {
                        return@subscribe
                    }
                }, { error ->
                    viewState.onError("Произощла ошибка")
                }
            )
    }

    fun delitMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao()!!.deleteMessage(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setListener(context, true)
            }, { error ->
                viewState.onError("")
            })
    }
}