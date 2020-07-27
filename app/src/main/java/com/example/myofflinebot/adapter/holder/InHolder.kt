package com.example.myofflinebot.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myofflinebot.R
import com.example.myofflinebot.data.db.entity.Message

class InHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.in_item, parent, false)) {
    private var inText: TextView? = null

    init {
        inText = itemView.findViewById(R.id.inText)
    }

    fun bind(message: Message) {
        inText?.text = message.mesegaPipla
    }
}