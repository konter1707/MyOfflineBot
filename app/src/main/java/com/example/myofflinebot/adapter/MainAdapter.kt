package com.example.myofflinebot.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import com.example.myofflinebot.adapter.holder.InHolder
import com.example.myofflinebot.adapter.holder.OutHolder
import com.example.myofflinebot.data.db.entity.Message

class MainAdapter(private val list: List<Message>, private  val delitListener: OnDelitListener) :
    Adapter<ViewHolder>() {
    override fun getItemViewType(position: Int): Int {
        if (list[position].out) {
            return 0
        }
        return 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        if (viewType == 0) {
            return OutHolder(inflater, parent)
        }
        return InHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        if (list[position].out == true) {
            val h: OutHolder = holder as OutHolder
            h.itemView.setOnClickListener { view ->
                delitListener.onClickItem(list[position])
            }
            h.itemView.setOnLongClickListener { view->
                delitListener.onLongClickItem(list[position],h.itemView)
                return@setOnLongClickListener true
            }
            h.bind(list[position])
        } else {
            val h: InHolder = holder as InHolder
            h.itemView.setOnClickListener {
                delitListener.onClickItem(list[position])
            }
            h.itemView.setOnLongClickListener { view->
                delitListener.onLongClickItem(list[position],h.itemView)
                return@setOnLongClickListener true
            }

            h.bind(list[position])
        }

    override fun getItemCount(): Int {
        return list.size
    }
    interface OnDelitListener{
        fun onClickItem(message: Message)
        fun onLongClickItem(message: Message, view:View)
    }
}