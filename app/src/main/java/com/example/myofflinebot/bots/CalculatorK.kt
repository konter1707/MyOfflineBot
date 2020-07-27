package com.example.myofflinebot.bots

import android.annotation.SuppressLint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.script.ScriptEngineManager

class CalculatorK {
    @SuppressLint("CheckResult")
    fun cfl(text: String) {
        s(text)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

            },{

            },{

            })
    }
fun s(text:String):Observable<String>{
    return Observable.create {subsciber->
        val manadger = ScriptEngineManager()
        val engine = manadger.getEngineByName("rhino")
        val otvet= engine.eval(text)
        subsciber.onNext(otvet.toString())
    }
}
}