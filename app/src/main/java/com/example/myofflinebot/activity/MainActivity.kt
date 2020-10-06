package com.example.myofflinebot.activity

import android.app.Activity
import android.app.WallpaperManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myofflinebot.R
import com.example.myofflinebot.adapter.MainAdapter
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.bots.ParseJokes
import com.example.myofflinebot.comon.CustomSharedPreferens
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.fragment.MyDialog
import com.example.myofflinebot.presentation.MainPresenter
import com.example.myofflinebot.view.MainView
import kotlinx.android.synthetic.main.listmessendger.*
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.panelmessendger.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter

class MainActivity : MvpAppCompatActivity(), MainView, MainAdapter.OnDelitListener,
    View.OnClickListener {
    @InjectPresenter
    lateinit var mainPresenter: MainPresenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        registerForContextMenu(rv)
        when (CustomSharedPreferens(BotJob.TAG_BOT).getValueSP(this)) {
            mTag[0] -> toolbar.title = getString(R.string.menu)
            mTag[1] -> toolbar.title = "Калькулятор"
            mTag[2] ->{
                toolbar.title = getString(R.string.jokes)
                val value=CustomSharedPreferens(BotJob.LIST_TAG_ANECDOTE).getValueSP(this)
                val strBuilder = StringBuilder()
                val listTag= mutableListOf<String>()
                value.split(",").forEach {
                    val textTag = it.split(":")[0]
                    strBuilder.append(textTag.plus("\n"))
                    listTag.add(textTag)
                }
                mainPresenter.viewState.setPanelMessendger(listTag)
            }
        }
        setSupportActionBar(toolbar)
        mainPresenter.setListener(this)
        send.setOnClickListener(this)
        gallery.setOnClickListener(this)
        val adapterEditText =
            ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mTag)
        autoTv.setAdapter(adapterEditText)
        autoTv.threshold = 1
        autoTv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val userText = adapterView.getItemAtPosition(i).toString()
            if (listtagjokes.visibility==View.VISIBLE &&(userText.trim()== mTag[0] || userText.trim()== mTag[1])){
                listtagjokes.visibility=View.GONE
            }
            val input=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            input.hideSoftInputFromWindow(autoTv.applicationWindowToken,InputMethodManager.HIDE_NOT_ALWAYS)
            mainPresenter.addUserMessage(this, setMessage(true, userText))
            mainPresenter.getMessageBot(this, userText)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.gallery -> {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
            }
            R.id.send -> {
                val userText: String = autoTv.text.toString()
                if (listtagjokes.visibility==View.VISIBLE &&(userText.trim()== mTag[0] || userText.trim()== mTag[1])){
                    listtagjokes.visibility=View.GONE
                }
                val input=getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(autoTv.applicationWindowToken,InputMethodManager.HIDE_NOT_ALWAYS)
                mainPresenter.addUserMessage(this, setMessage(true, userText))
                mainPresenter.getMessageBot(this, userText)
            }
        }
    }

    override fun starOnClick() {
        autoTv.setText("")
    }

    override fun onError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun setList(message: List<Message>) {
        rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
            rv.apply {
                adapter = MainAdapter(message, this@MainActivity)
            }
        }
    }

    override fun setTitleToolbar(titleToolbar: String) {
        toolbar.title = titleToolbar
    }

    override fun setPanelMessendger(list: MutableList<String>) {
        listtagjokes.visibility=View.VISIBLE
        listview.setOnItemClickListener { adapterView, view, position, l ->
            listtagjokes.visibility=View.GONE
            val textTag = list[position].split("(")[0].trim()
            mainPresenter.addUserMessage(this, setMessage(true, textTag))
            mainPresenter.getMessageBot(this, textTag)
        }
        val adapter=ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list)
        listview.adapter=adapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = data!!.data
            val imageStream = contentResolver.openInputStream(imageUri!!)!!
            val selectImageView: Bitmap = BitmapFactory.decodeStream(imageStream)
            val mWM = WallpaperManager.getInstance(this)
            mWM.setBitmap(selectImageView)
            Toast.makeText(this, "Ваши обои установлены", Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.clear -> {
                val manager = supportFragmentManager
                val myDialog = MyDialog(
                    mainPresenter,
                    setMessage(false, ""),
                    "Очистить все",
                    "Вы точно хотите очистить все?"
                )
                myDialog.show(manager, "tag")
            }
            R.id.exit -> {
                val manager = supportFragmentManager
                val myDialog = MyDialog(
                    mainPresenter,
                    setMessage(false, ""),
                    "Выход",
                    "Вы точно хотите выйти?"
                )
                myDialog.show(manager, "tag")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClickItem(message: Message) {
        val manager = supportFragmentManager
        val myDialog = MyDialog(
            mainPresenter,
            message,
            "Удалить",
            "Вы точно хотите удалить это сообщение"
        )
        myDialog.show(manager, "tag")
    }

    override fun onLongClickItem(message: Message, view: View) {
        val popapMenu = PopupMenu(this, view)
        popapMenu.inflate(R.menu.popap)
        popapMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.popapEdit -> {
                    popapEdit.visibility = View.VISIBLE
                    gallery.visibility = View.GONE
                    send.visibility = View.GONE
                    autoTv.setText(message.mesegaPipla)
                    popapEdit.setOnClickListener{
                        val editMessage = autoTv.text.toString()
                        mainPresenter.updateMessage(this, editMessage, message.id)
                        autoTv.setText("")
                        popapEdit.visibility = View.GONE
                        gallery.visibility = View.VISIBLE
                        send.visibility = View.VISIBLE
                    }
                    true

                }
                R.id.popapCopy -> {
                    val clipboardManager =
                        getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clipData = ClipData.newPlainText("message", message.mesegaPipla)
                    clipboardManager.setPrimaryClip(clipData)
                    Toast.makeText(applicationContext, "Копировано", Toast.LENGTH_LONG).show()
                    true
                }
                R.id.popapDelet -> {
                    val manager = supportFragmentManager
                    val myDialog = MyDialog(
                        mainPresenter,
                        message,
                        "Удалить",
                        "Вы точно хотите удалить это сообщение"
                    )
                    myDialog.show(manager, "tag")
                    true
                }
                else -> false
            }
        }
        popapMenu.show()
    }

    private fun setMessage(isOut: Boolean, text: String): Message {
        return Message(0, isOut, text)
    }
    companion object{
        const val GALLERY_REQUEST: Int = 1
        val mTag = arrayOf("/menu", "/calc", "/jokes")
    }
}