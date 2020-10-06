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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myofflinebot.R
import com.example.myofflinebot.adapter.MainAdapter
import com.example.myofflinebot.bots.BotJob
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
            mTag[0] -> toolbar.title = "Меню"
            mTag[1] -> toolbar.title = "Калькулятор"
            mTag[2] -> toolbar.title = "Анекдоты"
        }
        setSupportActionBar(toolbar)
        mainPresenter.setListener(this)
        send.setOnClickListener(this)
        galireya.setOnClickListener(this)
        val adapterEditText =
            ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, mTag)
        autoTv.setAdapter(adapterEditText)
        autoTv.threshold = 1
        autoTv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            val userText = adapterView.getItemAtPosition(i).toString()
            mainPresenter.addUserMessage(this, setMessage(true, userText))
            mainPresenter.getMessageBot(this, userText)
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.galireya -> {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, GALLERY_REQUEST)
            }
            R.id.send -> {
                val userText: String = autoTv.text.toString()
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
                    this,
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
                    this,
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
            this,
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
                    galireya.visibility = View.GONE
                    send.visibility = View.GONE
                    autoTv.setText(message.mesegaPipla)
                    popapEdit.setOnClickListener{
                        val editMessage = autoTv.text.toString()
                        mainPresenter.updateMessage(this, editMessage, message.id)
                        autoTv.setText("")
                        popapEdit.visibility = View.GONE
                        galireya.visibility = View.VISIBLE
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
                        this,
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