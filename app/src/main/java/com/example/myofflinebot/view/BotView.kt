package com.example.myofflinebot.view

import moxy.MvpView
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType

@StateStrategyType(AddToEndSingleStrategy::class)
interface BotView:MvpView {
    fun setMessageBot(otvetBot:String)
}