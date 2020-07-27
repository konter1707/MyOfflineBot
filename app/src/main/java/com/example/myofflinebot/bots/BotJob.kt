package com.example.myofflinebot.bots

import android.content.Context
import android.content.SharedPreferences
import com.example.myofflinebot.bots.jokes.Anecdot
import com.example.myofflinebot.bots.jokes.ModelJokes
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.jsoup.Jsoup
import javax.script.ScriptEngineManager
import javax.script.ScriptException
import kotlin.random.Random

class BotJob(context: Context) {
    val PRIVATE_MODE: Int = 0
    val PREF_NAME: String = "name_tag"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    fun listBotJob(text: String): Single<String> {
        return Single.create { subsciber ->
            if (isText(text)) {
                if (getValueText("tag").equals(text)) {
                    if (getValueText("tag").equals("/menu")) {
                        subsciber.onSuccess("Вы в меню")
                        return@create
                    }
                    subsciber.onSuccess("Вы уже находитесь тут. Что бы перейти в меню введите тэг /menu")
                    return@create
                }
             else {
                    save("tag", text)
                    if (text.trim().equals("/menu")) {
                        subsciber.onSuccess("Вы в меню. \n 1. Калькулятор \n 2. Анекдоты")
                        return@create
                    } else if (text.trim().equals("/calc")) {
                        subsciber.onSuccess("Вы перешли в калькулятор. \n Введите свой пример")
                        return@create
                    }else if(text.trim().equals("/jokes")){
                        subsciber.onSuccess(("Вы перешли в анекдоты. \n На какую тему вы хотите смотреть анекдоты?" +
                                " Если на любую тему, то тогда так и напишите любой"))
                    }
                }
            }else{
                    if (getValueText("tag").equals("/calc")) {
                        try {
                            val manadger = ScriptEngineManager()
                            val engine = manadger.getEngineByName("rhino")
                            val otvet = engine.eval(text).toString()
                            val floatResult: Float = otvet.toFloat()
                            val intResult = floatResult.toInt()
                            val displayedResult =
                                if (floatResult - intResult == 0f) "" + intResult else "" + floatResult
                            subsciber.onSuccess(displayedResult)
                            return@create
                        }catch (script:ScriptException){
                            subsciber.onError(script)
                        }catch (numberFormat:NumberFormatException){
                            subsciber.onError(numberFormat)
                        }
                    }
                if (getValueText("tag").equals("/jokes") && text.trim().equals("Любой") || text.trim().equals("Да")){
                    val randomNumber=Random.nextInt(0,3127)
                    val doc= Jsoup.connect("https://4tob.ru/anekdots/"+randomNumber).get()
                    val element=doc.select("div.text")
                    subsciber.onSuccess(element.text()+"\n Ещё?")
                }
            }
        }
    }

    private fun isText(text: String): Boolean {
        return text.trim()[0].equals('/')
    }

    fun save(KEY_NAME: String, text: String) {
        val editor: SharedPreferences.Editor = sharedPref.edit()
        editor.putString(KEY_NAME, text)
        editor.apply()
    }

    fun getValueText(KEY_NAME: String): String {
        return sharedPref.getString(KEY_NAME, "/calc").toString()
    }
}