package com.example.myofflinebot.bots

import android.content.Context
import com.example.myofflinebot.comon.CustomSharedPreferens
import io.reactivex.Single
import org.jsoup.Jsoup
import java.lang.StringBuilder
import kotlin.random.Random

class ParseJokes(val context: Context) {

    fun parseJokes(selectTag:String): Single<String> {
        return Single.create { subscriber ->
            try {
                val listTag=CustomSharedPreferens(BotJob.LIST_TAG_ANECDOTE).getValueSP(context).split(",")
                listTag.forEach {
                    val urlJokes=it.split(":")
                    val textTag = urlJokes[0].split("(")[0].trim()
                    if (textTag==selectTag){
                        val doc = Jsoup.connect(URL_ANECDOTE.plus(urlJokes[1])).get()
                        val elements = doc.select("div.text>p")
                        val lastNumber = elements.size
                        val randomNumber = Random.nextInt(0, lastNumber)
                        val element = elements[randomNumber]
                        textJokes = if (!element.select("br").isEmpty())
                            element.html().replace("<br>", "\n")
                         else element.text()
                        subscriber.onSuccess(textJokes.plus("\n Ещё?"))
                        return@create
                    }
                    }
                subscriber.onSuccess(textJokes)
                return@create
            } catch (e: Exception) {
                subscriber.onError(e.fillInStackTrace())
            }
        }
    }

    fun setTagJokes():Single<String> {
         return Single.create{
            val tagJokesBuilder=StringBuilder()
             val doc = Jsoup.connect(URL_TAG).get()
            val els = doc.select("ul.tag-list>li>a")
            els.forEach {el->
                val url = el.attr("href")
                tagJokesBuilder.append(el.text().plus(":").plus(url).plus(","))
              }
             CustomSharedPreferens(BotJob.LIST_TAG_ANECDOTE).save(context,tagJokesBuilder.toString())
             it.onSuccess(tagJokesBuilder.toString())
         }
    }
    companion object {
        const val URL_TAG = "https://4tob.ru/anekdots/tag"
        const val URL_ANECDOTE = "https://4tob.ru"
        private var textJokes: String = "Привет"
    }
}
