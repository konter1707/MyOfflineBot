package com.example.myofflinebot.bots

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.example.myofflinebot.presentation.MainPresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.test.withTestContext

class ListWorcsBot(context: Context)  {
    val ctx:Context=context
    val PRIVATE_MODE: Int = 0
    val PREF_NAME: String = "name_tag"
    val sharedPref: SharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
    @SuppressLint("ApplySharedPref", "CheckResult")
    public fun setMessageBot(text: String): String {
       val tag: String = getValueText("tag")
        if (text.trim()[0].equals('/')) {
            if (getValueText("tag").equals(text)) {
                if (getValueText("tag").equals("/menu")) {
                    return "Вы в меню. \n 1. Калькулятор"
                }
                return "Вы уже находитесь тут. Что бы перейти в меню введите тэг /menu"
            } else {
                save("tag", text)
                if (text.trim().equals("/menu")) {
                    return "Вы в меню. \n 1. Калькулятор"
                } else if (text.trim().equals("/calc")) {
                    return "Вы перешли в калькулятор. \n Введите свой пример"
                } else if (text.trim().equals("/product_cost")) {
                    return "Вы перешли сюда что бы узнать стоимость рецепта. Введите ингридиенты своего рецепта через запятую"
                }
            }
        } else {
            if (getValueText("tag").equals("/calc")) {
                CalculatorK().cfl(text)
                return ""
            }
            if (getValueText("tag").equals("/product_cost")) {
                val pr = ProductCost().productCost(text)
                return "Вот сумма вашего рецепта " + pr + "рублей"
            }
            if (text.trim().equals("Привет")) return "Привет. Как дела?"
            if (text.trim()
                    .equals("Хорошо. У тебя как?")
            ) return "Плохо. Не могу научится решать примеры("
            if (text.trim().equals("Фуу...")) return "..."
        }
        return "Я не знаю что вы тут написали"
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