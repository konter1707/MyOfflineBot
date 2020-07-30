package com.example.myofflinebot.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myofflinebot.R
import com.example.myofflinebot.adapter.MainAdapter
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.fragment.MyDialog
import com.example.myofflinebot.presentation.MainPresenter
import com.example.myofflinebot.view.MainView
import kotlinx.android.synthetic.main.listmessendger.*
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.panelmessendger.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter


class MainActivity : MvpAppCompatActivity(), MainView, MainAdapter.OnDelitListener {

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        registerForContextMenu(rv)
        setSupportActionBar(toolbar)
        val mTag = arrayOf("/menu", "/calc", "/jokes")
        val adapterEditText =
            ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mTag)
        autoTv.setAdapter(adapterEditText)
        autoTv.threshold = 1
        mainPresenter.setListener(this, true)
        imageView.setOnClickListener { view ->
            val userText: String = autoTv.text.toString()
            mainPresenter.addUserMessage(this, setMessage(true, userText))
            mainPresenter.setListener(this, true)
            mainPresenter.getMessageBot(this, userText)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delet -> {
                mainPresenter.delitListMessaga(this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun starOnClick(text: String) {
        mainPresenter.setListener(this, true)
        autoTv.setText("")
    }

    override fun onError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun setList(message: List<Message>) {
        if (message.size == 0) {
            mainPresenter.getMessageBot(this, "/menu")
            return;
        } else {
            rv.apply {
                layoutManager = LinearLayoutManager(this@MainActivity).apply {
                    stackFromEnd = true
                }

                rv.apply {
                    adapter = MainAdapter(message, this@MainActivity)
                }
            }
        }
    }

    override fun setMessageBot(otvetBot: String) {
        mainPresenter.addBotMessage(this, setMessage(false, otvetBot))
        mainPresenter.setListener(this, false)
    }

    override fun delite(messageList: List<Message>) {
    }

    override fun onClickItem(message: Message) {
        val manager = supportFragmentManager
        val myDialog = MyDialog(this, mainPresenter, message)
        myDialog.show(manager, "tag")
    }

    override fun onLongClickItem(message: Message, view: View) {
        val popapMenu = PopupMenu(this, view)
        popapMenu.inflate(R.menu.popap)
        popapMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.redaction -> {
                    true
                }
                R.id.exit -> {
                    val clipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("message", message.mesegaPipla)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(applicationContext, "Копировано", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.delet -> {
                    val manager = supportFragmentManager
                    val myDialog = MyDialog(this, mainPresenter, message)
                    myDialog.show(manager, "tag")
                    true
                }
                else -> false
            }
        }
        popapMenu.show()
    }

    private fun setMessage(isOut: Boolean, text: String): Message {
        val message: Message = Message(0, isOut, text)
        return message
    }
}