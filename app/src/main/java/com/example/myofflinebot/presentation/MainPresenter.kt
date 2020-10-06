package com.example.myofflinebot.presentation

import android.content.Context
import com.example.myofflinebot.activity.MainActivity
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.comon.BasePresenter
import com.example.myofflinebot.data.db.MessageDB
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState

@InjectViewState
class MainPresenter() : BasePresenter<MainView>() {
    fun updateMessage(context: Context, messagePeople: String, id: Long) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().updateMessage(messagePeople, id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    setListener(context)
                }, {

                }
            ).autoDisposable()
    }

    fun deleteListMessage(context: Context) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().delite()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    setListener(context)
                },
                {}).autoDisposable()
    }


    fun addUserMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().insertRx(message)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    viewState.starOnClick()
                    setListener(context)
                },
                { error ->
                    viewState.onError("" + error)
                }).autoDisposable()
    }


    fun getMessageBot(context: Context, userText: String) {
        BotJob(context).listBotJob(userText, this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                addBotMessage(context, Message(0, false, it))
            }, {
                addBotMessage(context, Message(0, false, it.message.toString()))
            }
            ).autoDisposable()
    }

    private fun addBotMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().insertRx(message)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                {
                    setListener(context)
                },
                { error ->
                    viewState.onError("" + error)
                }).autoDisposable()
    }

    fun setListener(context: Context) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().getMessage()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { result ->
                    if (result.isEmpty()) {
                        getMessageBot(context, MainActivity.mTag[0])
                        return@subscribe
                    }
                    viewState.setList(result)
                }, { error ->
                    viewState.onError("Произощла ошибка")
                }
            ).autoDisposable()
    }

    fun deleteMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().deleteMessage(message)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                setListener(context)
            }, { error ->
                viewState.onError("")
            }).autoDisposable()
    }

    fun setTitleToolbar(titleToolbar: String) {
        viewState.setTitleToolbar(titleToolbar)
    }
}

