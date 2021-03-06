package com.example.myofflinebot.view


import com.example.myofflinebot.data.db.entity.Message
import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface MainView : MvpView {
    fun starOnClick()
    fun onError(text: String)
    fun setList(message: List<Message>)
    fun setTitleToolbar(titleToolbar:String)
    fun setPanelMessendger( list: MutableList<String>)
}