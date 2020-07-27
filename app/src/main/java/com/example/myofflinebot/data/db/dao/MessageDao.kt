package com.example.myofflinebot.data.db.dao

import androidx.room.*
import com.example.myofflinebot.data.db.entity.Message
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRx(message: Message): Completable

    @Update
    fun updateMessage(message: Message)

    @Delete
    fun deleteMessage(message: Message): Completable

    @Query("SELECT * FROM Message")
    fun getMessage(): Single<List<Message>>
}