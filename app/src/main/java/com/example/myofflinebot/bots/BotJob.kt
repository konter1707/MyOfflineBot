package com.example.myofflinebot.bots

import android.annotation.SuppressLint
import android.content.Context
import com.example.myofflinebot.comon.CustomSharedPreferens
import com.example.myofflinebot.presentation.MainPresenter
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.script.ScriptEngineManager

@SuppressLint("CheckResult")
class BotJob(private val context: Context) {
    fun listBotJob(text: String, mainPresenter: MainPresenter): Single<String> {
        return Single.create { subsciber ->
            val valueSP = tagCustomSP0.getValueSP(context)
            val trimmedText = text.trim()
            if (isTag(text)) {
                if (valueSP == trimmedText && valueSP != TAG_MENU) {
                    subsciber.onSuccess("Вы уже находитесь тут. Что бы перейти в меню введите тэг /menu")
                } else {
                    tagCustomSP0.save(context, text)
                    when (trimmedText) {
                        TAG_MENU -> {
                            mainPresenter.setTitleToolbar("Меню")
                            subsciber.onSuccess("Вы в меню. \n 1. Калькулятор \n 2. Анекдоты")
                        }

                        TAG_CALC -> {
                            subsciber.onSuccess("Вы перешли в калькулятор. \n Введите свой пример")
                            mainPresenter.setTitleToolbar("Калькулятор")
                        }

                        TAG_JOKES -> {
                            mainPresenter.setTitleToolbar("Анекдоты")
                            getTagJokes(subsciber, mainPresenter)
                        }

                        else -> subsciber.onSuccess("Неизвестная команда.")
                    }
                }
                return@create
            } else {
                when (valueSP) {
                    TAG_CALC -> {
                        try {
                            val manager = ScriptEngineManager()
                            val engine = manager.getEngineByName("rhino")
                            val answer = engine.eval(text).toString()
                            val floatResult: Float = answer.toFloat()
                            val intResult = floatResult.toInt()
                            val displayedResult =
                                if (floatResult - intResult == 0f) "" + intResult else "" + floatResult
                            subsciber.onSuccess(displayedResult)
                        } catch (error: Exception) {
                            subsciber.onError(error)
                        }
                        return@create
                    }

                    TAG_JOKES -> {
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
                }

                if (trimmedText == "Да") {
                    getTagJokes(subsciber, mainPresenter)
                    return@create
                }

                subsciber.onSuccess("Данная команда не известна")
            }
        }
    }

    private fun getTagJokes(subscriber: SingleEmitter<String>, mainPresenter: MainPresenter) {
        var textTag: String
        val value = CustomSharedPreferens(LIST_TAG_ANECDOTE).getValueSP(context)

        fun parseAndSendTags(raw: String) {
            val strBuilder = StringBuilder()
            val listTag = mutableListOf<String>()
            raw.split(",").forEach { res ->
                textTag = res.split(":")[0]
                strBuilder.append(textTag.plus("\n"))
                listTag.add(textTag)
            }

            mainPresenter.viewState.setPanelMessendger(listTag)
            subscriber.onSuccess(strBuilder.toString())
        }

        if (value == "0") {
            ParseJokes(context).setTagJokes()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe({ result ->
                    parseAndSendTags(result)
                }, {
                    subscriber.onError(it)
                })
        } else {
            parseAndSendTags(value)
        }
    }

    private fun isTag(text: String): Boolean {
        return text.trim()[0] == '/'
    }

    companion object {
        const val TAG_MENU = "/menu"
        const val TAG_CALC = "/calc"
        const val TAG_JOKES = "/jokes"
        const val TAG_BOT: Int = 0
        const val LIST_TAG_ANECDOTE: Int = 1
        private val tagCustomSP0 = CustomSharedPreferens(TAG_BOT)
    }
}