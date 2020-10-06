package com.example.myofflinebot.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myofflinebot.data.db.dao.MessageDao
import com.example.myofflinebot.data.db.entity.Message

@Database(entities = [Message::class], version = 1)
abstract class MessageDB : RoomDatabase() {
    abstract fun getMessageDao(): MessageDao
    companion object {
        var INSTANCE: MessageDB? = null
        fun getAppDateBase(context: Context): MessageDB? {
            if (INSTANCE == null) {
                synchronized(MessageDB::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        MessageDB::class.java,
                        "myDB"
                    ).build()
                }
            }
            return INSTANCE
        }
    }
}