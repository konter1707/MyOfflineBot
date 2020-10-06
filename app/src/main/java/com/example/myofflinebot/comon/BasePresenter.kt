package com.example.myofflinebot.comon

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import moxy.MvpPresenter
import moxy.MvpView

open class BasePresenter<T:MvpView>:MvpPresenter<T>() {
    private val subs=CompositeDisposable()
    protected  fun Disposable.autoDisposable(){
        subs.add(this)
    }
    protected fun dispose(){
        subs.dispose()
    }
    override fun onDestroy() {
        subs.clear()
        super.onDestroy()
    }

}