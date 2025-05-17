package com.example.myofflinebot.manager

import android.content.Context
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.comon.CustomSharedPreferens

class PreferencesManager(private val context: Context) {

    fun getCurrentTag(): String {
        return CustomSharedPreferens(BotJob.TAG_BOT).getValueSP(context)
    }

    fun parseJokeTags(tagString: String): List<String> {
        return tagString
            .split(",")
            .mapNotNull { it.split(":").getOrNull(0)?.trim() }
            .filter { it.isNotEmpty() }
    }
}
