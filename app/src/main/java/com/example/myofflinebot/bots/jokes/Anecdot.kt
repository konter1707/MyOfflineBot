package com.example.myofflinebot.bots.jokes

import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Anecdot {
    fun addAnecdots(): Single<List<ModelJokes>> {
        return Single.create {subsciber->
            val list:MutableList<ModelJokes> = mutableListOf()
            val doc=Jsoup.parse("https://4tob.ru/anekdots/")
            val element=doc.select("div.text>p")
            list.add(ModelJokes(element.text(),""))
            subsciber.onSuccess(list)
        }
    }
}