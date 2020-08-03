package com.example.myofflinebot.presentation

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.comon.BasePresenter
import com.example.myofflinebot.data.db.MessageDB
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.view.MainView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState

@InjectViewState
@SuppressLint("CheckResult")
class MainPresenter : BasePresenter<MainView>() {
    fun delitListMessaga(context: Context) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().delite()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    setListener(context, true)
                    Toast.makeText(context, "Удаленно все", Toast.LENGTH_LONG).show()
                },
                {

                }).autoDisposable()
    }

    fun addUserMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().insertRx(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    viewState.starOnClick("")
                },
                { error ->
                    viewState.onError("" + error)
                }).autoDisposable()
    }

    fun getMessageBot(context: Context, userText: String) {
        BotJob(context).listBotJob(userText)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                addBotMessage(context, Message(0,false, it))
                setListener(context, false)
            }, {
                addBotMessage(context, Message(0,false, "Произошла ошибка. Проверьте написанное!"))
                setListener(context, false)
            }
            ).autoDisposable()
    }

    fun addBotMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().insertRx(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    viewState.starOnClick("")
                },
                { error ->
                    viewState.onError("" + error)
                }).autoDisposable()
    }

    fun setListener(context: Context, isOut: Boolean) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().getMessage()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { result ->
                    if(result.isEmpty()){
                        getMessageBot(context, "/menu")
                    }
                    viewState.setList(result)
                    if (!isOut) {
                        return@subscribe
                    }
                }, { error ->
                    viewState.onError("Произощла ошибка")
                }
            ).autoDisposable()
    }

    fun delitMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().deleteMessage(message)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                setListener(context, true)
            }, { error ->
                viewState.onError("")
            }).autoDisposable()
    }
}