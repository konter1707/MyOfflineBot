package com.example.myofflinebot.adapter.holder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myofflinebot.R
import com.example.myofflinebot.data.db.entity.Message

class OutHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.out_item, parent, false)) {
    private var outText: TextView? = null

    init {
        outText = itemView.findViewById(R.id.outText)
    }

    fun bind(message: Message) {
        outText?.text = message.mesegaPipla
    }
}