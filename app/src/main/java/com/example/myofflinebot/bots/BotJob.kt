package com.example.myofflinebot.bots

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.example.myofflinebot.comon.CustomSharedPreferens
import com.example.myofflinebot.presentation.MainPresenter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.script.ScriptEngineManager
import javax.script.ScriptException

@SuppressLint("CheckResult")
class BotJob(val context: Context) {
    fun listBotJob(text: String, mainPresenter: MainPresenter): Single<String> {
        return Single.create { subsciber ->
            val valueSP = tagCustomSP0.getValueSP(context)
            if (isTag(text)) {
                if (valueSP == text && valueSP != "/menu") {
                    subsciber.onSuccess("Вы уже находитесь тут. Что бы перейти в меню введите тэг /menu")
                    return@create
                } else {
                    tagCustomSP0.save(context, text)
                    when {
                        text.trim() == "/menu" -> {
                            mainPresenter.setTitleToolbar("Меню")
                            subsciber.onSuccess("Вы в меню. \n 1. Калькулятор \n 2. Анекдоты")
                            return@create
                        }
                        text.trim() == "/calc" -> {
                            subsciber.onSuccess("Вы перешли в калькулятор. \n Введите свой пример")
                            mainPresenter.setTitleToolbar("Калькулятор")
                            return@create
                        }
                        text.trim() == "/jokes" -> {
                            mainPresenter.setTitleToolbar("Анекдоты")
                            getTagJokes(subsciber,mainPresenter)
                        }
                    }
                }
            } else {
                if (valueSP == "/calc") {
                    try {
                        val manager = ScriptEngineManager()
                        val engine = manager.getEngineByName("rhino")
                        val answer = engine.eval(text).toString()
                        val floatResult: Float = answer.toFloat()
                        val intResult = floatResult.toInt()
                        val displayedResult =
                            if (floatResult - intResult == 0f) "" + intResult else "" + floatResult
                        subsciber.onSuccess(displayedResult)
                        return@create
                    } catch (script: ScriptException) {
                        subsciber.onError(script)
                    } catch (numberFormat: NumberFormatException) {
                        subsciber.onError(numberFormat)
                    }
                }
                if (valueSP == "/jokes") {
                    ParseJokes(context).parseJokes(text)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe({
                            subsciber.onSuccess(it)
                            return@subscribe
                        }, {
                            subsciber.onError(it)
                            return@subscribe
                        })
                }
                if (text.trim()=="Да") getTagJokes(subsciber,mainPresenter)
            }
        }
    }

    private fun getTagJokes(subscriber: SingleEmitter<String>,mainPresenter: MainPresenter) {
        var textTag: String
        val value = CustomSharedPreferens(LIST_TAG_ANECDOTE).getValueSP(context)
        if (value == "0") {
            ParseJokes(context).setTagJokes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({
                    val strBuilder = StringBuilder()
                    val listTag= mutableListOf<String>()
                    it.split(",").forEach {res->
                        textTag = res.split(":")[0]
                        strBuilder.append(textTag.plus("\n"))
                        listTag.add(textTag)
                    }
                    mainPresenter.viewState.setPanelMessendger(listTag)
                    subscriber.onSuccess(strBuilder.toString())
                }, {
                    subscriber.onError(it)
                })
        } else {
            val strBuilder = StringBuilder()
            val listTag= mutableListOf<String>()
            value.split(",").forEach {
                textTag = it.split(":")[0]
                strBuilder.append(textTag.plus("\n"))
                listTag.add(textTag)
            }
            mainPresenter.viewState.setPanelMessendger(listTag)
            subscriber.onSuccess(strBuilder.toString())
        }
    }

    private fun isTag(text: String): Boolean {
        return text.trim()[0] == '/'
    }

    companion object {
        const val TAG_BOT: Int = 0
        const val LIST_TAG_ANECDOTE: Int = 1
        private val tagCustomSP0 = CustomSharedPreferens(TAG_BOT)
    }
}