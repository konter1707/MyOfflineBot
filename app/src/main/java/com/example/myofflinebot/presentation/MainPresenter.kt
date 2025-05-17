package com.example.myofflinebot.presentation

import android.content.Context
import android.net.Uri
import com.example.myofflinebot.activity.MainActivity
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.comon.BasePresenter
import com.example.myofflinebot.data.db.MessageDB
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.manager.PreferencesManager
import com.example.myofflinebot.view.MainView
import com.example.myofflinebot.wallpaper.WallpaperInteractor
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import moxy.InjectViewState
import kotlin.coroutines.CoroutineContext

@InjectViewState
class MainPresenter(
    private val preferencesManager: PreferencesManager,
    private val wallpaperInteractor: WallpaperInteractor
) : BasePresenter<MainView>(), CoroutineScope {

    fun updateMessage(context: Context, messagePeople: String, id: Long) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().updateMessage(messagePeople, id)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { setListener(context) }
            .autoDisposable()
    }

    fun deleteListMessage(context: Context) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().delete()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe { setListener(context) }
            .autoDisposable()
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
                }
            )
            .autoDisposable()
    }

    fun getMessageBot(context: Context, userText: String) {
        BotJob(context).listBotJob(userText, this)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { addBotMessage(context, Message(0, false, it)) },
                { addBotMessage(context, Message(0, false, it.message.toString())) }
            )
            .autoDisposable()
    }

    private fun addBotMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().insertRx(message)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { setListener(context) },
                { error -> viewState.onError("" + error) }
            )
            .autoDisposable()
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
                },
                { viewState.onError("Произошла ошибка") }
            )
            .autoDisposable()
    }

    fun deleteMessage(context: Context, message: Message) {
        MessageDB.getAppDateBase(context)!!.getMessageDao().deleteMessage(message)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe(
                { setListener(context) },
                { viewState.onError("") }
            )
            .autoDisposable()
    }

    fun setTitleToolbar(titleToolbar: String) {
        viewState.setTitleToolbar(titleToolbar)
    }

    fun onImageSelected(uri: Uri) {
        launch {
            val result = wallpaperInteractor.setWallpaper(uri)
            if (result.isSuccess) {
//                viewState.showToast("Обои установлены")
            } else {
//                viewState.showToast("Ошибка установки обоев: ${result.exceptionOrNull()?.message}")
            }        }
    }

    fun onViewReady() {
        when (val currentTag = preferencesManager.getCurrentTag()) {
            BotJob.TAG_MENU -> setTitleToolbar("Меню")
            BotJob.TAG_CALC -> setTitleToolbar("Калькулятор")
            BotJob.TAG_JOKES -> {
                viewState.setTitleToolbar("Анекдоты")
                viewState.setPanelMessendger(
                    preferencesManager.parseJokeTags(currentTag).toMutableList()
                )
            }
        }

    }

    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main.immediate + job

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}
