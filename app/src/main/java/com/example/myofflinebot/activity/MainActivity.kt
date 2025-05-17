package com.example.myofflinebot.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myofflinebot.R
import com.example.myofflinebot.adapter.MainAdapter
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.databinding.ListmessendgerBinding
import com.example.myofflinebot.databinding.MainBinding
import com.example.myofflinebot.databinding.PanelmessendgerBinding
import com.example.myofflinebot.fragment.MyDialog
import com.example.myofflinebot.manager.PreferencesManager
import com.example.myofflinebot.presentation.MainPresenter
import com.example.myofflinebot.utils.KeyboardUtils
import com.example.myofflinebot.view.MainView
import com.example.myofflinebot.wallpaper.WallpaperInteractorImpl
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter

class MainActivity : MvpAppCompatActivity(), MainView, MainAdapter.OnDelitListener,
    View.OnClickListener {

    private val mainPresenter by moxyPresenter {
        MainPresenter(
            PreferencesManager(applicationContext),
            WallpaperInteractorImpl(applicationContext),
        )
    }

    private lateinit var binding: MainBinding
    private lateinit var bindingPanel: PanelmessendgerBinding
    private lateinit var bindingListMessenger: ListmessendgerBinding
    private lateinit var listTagJokes: LinearLayout

    private val pickImage = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { mainPresenter.onImageSelected(uri) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setContentView(binding.root)
        registerForContextMenu(binding.toolbar)
        listTagJokes = bindingPanel.listtagjokes
        mainPresenter.onViewReady()
        setSupportActionBar(binding.toolbar)
        initListener()
        setupAutoComplete()
    }

    override fun onClick(view: View?) {
        when (view) {
            bindingPanel.gallery -> pickImage.launch("image/*")

            bindingPanel.send -> {
                val userText: String = bindingPanel.autoTv.text.toString()
                if (listTagJokes.isVisible && (userText.trim() == mTag[0] || userText.trim() == mTag[1])) {
                    listTagJokes.visibility = View.GONE
                }
                val input = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(
                    bindingPanel.autoTv.applicationWindowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                mainPresenter.apply {
                    addUserMessage(this@MainActivity, setMessage(true, userText))
                    getMessageBot(this@MainActivity, userText)
                }
            }
        }
    }

    override fun starOnClick() {
        bindingPanel.autoTv.setText("")
    }

    override fun onError(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
    }

    override fun setList(message: List<Message>) {
        bindingListMessenger.rv.apply {
            layoutManager = LinearLayoutManager(this@MainActivity).apply {
                stackFromEnd = true
            }
            adapter = MainAdapter(message, this@MainActivity)
        }
    }

    override fun setTitleToolbar(titleToolbar: String) {
        binding.toolbar.title = titleToolbar
    }

    override fun setPanelMessendger(list: MutableList<String>) {
        bindingPanel.listtagjokes.visibility = View.VISIBLE
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        bindingPanel.listview.apply {
            setOnItemClickListener { _, _, position, _ ->
                listTagJokes.visibility = View.GONE
                val textTag = list[position].split("(")[0].trim()
                mainPresenter.addUserMessage(this@MainActivity, setMessage(true, textTag))
                mainPresenter.getMessageBot(this@MainActivity, textTag)
            }
            adapter = arrayAdapter
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
        val popupMenu = PopupMenu(this, view)
        popupMenu.inflate(R.menu.popup)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.popapEdit -> {
                    bindingPanel.apply {
                        popapEdit.visibility = View.VISIBLE
                        gallery.visibility = View.GONE
                        send.visibility = View.GONE
                        autoTv.setText(message.mesegaPipla)
                        popapEdit.setOnClickListener {
                            val editMessage = autoTv.text.toString()
                            mainPresenter.updateMessage(this@MainActivity, editMessage, message.id)
                            autoTv.setText("")
                            popapEdit.visibility = View.GONE
                            gallery.visibility = View.VISIBLE
                            send.visibility = View.VISIBLE
                        }

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

        popupMenu.show()
    }

    private fun initBinding() {
        binding = MainBinding.inflate(layoutInflater)
        bindingPanel = PanelmessendgerBinding.bind(binding.root.findViewById(R.id.buttonPanel))
        bindingListMessenger =
            ListmessendgerBinding.bind(binding.root.findViewById(R.id.container_list))

    }

    private fun initListener() {
        mainPresenter.setListener(this)
        bindingPanel.send.setOnClickListener(this)
        bindingPanel.gallery.setOnClickListener(this)
    }

    private fun setupAutoComplete() {
        val adapterEditText = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mTag)
        bindingPanel.autoTv.apply {
            setAdapter(adapterEditText)
            threshold = 1
            onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val userText = adapterView.getItemAtPosition(i).toString()
                if (listTagJokes.isVisible && (userText.trim() == mTag[0] || userText.trim() == mTag[1])) {
                    listTagJokes.visibility = View.GONE
                }
                KeyboardUtils.hide(this@MainActivity)
                mainPresenter.apply {
                    addUserMessage(this@MainActivity, setMessage(true, userText))
                    getMessageBot(this@MainActivity, userText)
                }
            }
        }
    }

    private fun setMessage(isOut: Boolean, text: String): Message {
        return Message(0, isOut, text)
    }

    companion object {
        val mTag = arrayOf(BotJob.TAG_MENU, BotJob.TAG_CALC, BotJob.TAG_JOKES)
    }
}