package com.example.myofflinebot.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Message(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val out: Boolean,
    val mesegaPipla: String
)
