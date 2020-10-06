package com.example.myofflinebot.data.db.dao

import androidx.room.*
import com.example.myofflinebot.data.db.entity.Message
import io.reactivex.Completable
import io.reactivex.Single

@Dao
interface MessageDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertRx(message: Message): Completable
    @Delete
    fun deleteMessage(message: Message): Completable

    @Query("DELETE FROM Message")
    fun delite(): Completable

    @Query("SELECT * FROM Message")
    fun getMessage(): Single<List<Message>>
    @Query("UPDATE message SET mesegaPipla=:messagePipla WHERE id=:id")
    fun  updateMessage(messagePipla: String,id: Long):Completable

}