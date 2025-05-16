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
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myofflinebot.R
import com.example.myofflinebot.adapter.MainAdapter
import com.example.myofflinebot.bots.BotJob
import com.example.myofflinebot.comon.CustomSharedPreferens
import com.example.myofflinebot.data.db.entity.Message
import com.example.myofflinebot.databinding.MainBinding
import com.example.myofflinebot.databinding.PanelmessendgerBinding
import com.example.myofflinebot.fragment.MyDialog
import com.example.myofflinebot.presentation.MainPresenter
import com.example.myofflinebot.view.MainView
import moxy.presenter.InjectPresenter
import androidx.core.view.isVisible
import com.example.myofflinebot.databinding.ListmessendgerBinding
import moxy.MvpAppCompatActivity

class MainActivity : MvpAppCompatActivity(), MainView, MainAdapter.OnDelitListener,
    View.OnClickListener {

    @InjectPresenter
    lateinit var mainPresenter: MainPresenter
    private lateinit var binding: MainBinding
    private lateinit var bindingPanel: PanelmessendgerBinding
    private lateinit var bindingListMessenger: ListmessendgerBinding
    private lateinit var listTagJokes: LinearLayout
    private lateinit var selectedImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        registerForContextMenu(binding.toolbar)
        bindingPanel = PanelmessendgerBinding.bind(binding.root.findViewById(R.id.buttonPanel))
        bindingListMessenger =
            ListmessendgerBinding.bind(binding.root.findViewById(R.id.container_list))
        listTagJokes = bindingPanel.listtagjokes
        when (val currentTag = CustomSharedPreferens(BotJob.TAG_BOT).getValueSP(this)) {
            BotJob.TAG_MENU -> setTitleToolbar(getString(R.string.menu))
            BotJob.TAG_CALC -> setTitleToolbar("Калькулятор")
            BotJob.TAG_JOKES -> {
                setTitleToolbar(getString(R.string.jokes))
                val listTag = currentTag
                    .split(",")
                    .mapNotNull { it.split(":").getOrNull(0)?.trim() }
                    .filter { it.isNotEmpty() }
                setPanelMessendger(listTag.toMutableList())
            }
        }
        setSupportActionBar(binding.toolbar)
        selectedImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data = result.data
                    val imageUri: Uri? = data?.data
                    if (imageUri != null) {
                        val imageStream = contentResolver.openInputStream(imageUri)
                        val selectImageView: Bitmap =
                            BitmapFactory.decodeStream(imageStream)
                        val mWM = WallpaperManager.getInstance(this)
                        mWM.setBitmap(selectImageView)
                        Toast.makeText(this, "Ваши обои установлены", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        mainPresenter.setListener(this)
        bindingPanel.send.setOnClickListener(this)
        bindingPanel.gallery.setOnClickListener(this)

        val adapterEditText = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, mTag)
        bindingPanel.autoTv.apply {
            setAdapter(adapterEditText)
            threshold = 1
            onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
                val userText = adapterView.getItemAtPosition(i).toString()
                if (listTagJokes.isVisible && (userText.trim() == mTag[0] || userText.trim() == mTag[1])) {
                    listTagJokes.visibility = View.GONE
                }
                val input = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                input.hideSoftInputFromWindow(
                    applicationWindowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
                mainPresenter.addUserMessage(this@MainActivity, setMessage(true, userText))
                mainPresenter.getMessageBot(this@MainActivity, userText)
            }
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            bindingPanel.gallery -> {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                selectedImageLauncher.launch(intent)
            }

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
                    bindingPanel.popapEdit.visibility = View.VISIBLE
                    bindingPanel.gallery.visibility = View.GONE
                    bindingPanel.send.visibility = View.GONE
                    bindingPanel.autoTv.setText(message.mesegaPipla)
                    bindingPanel.popapEdit.setOnClickListener {
                        val editMessage = bindingPanel.autoTv.text.toString()
                        mainPresenter.updateMessage(this, editMessage, message.id)
                        bindingPanel.autoTv.setText("")
                        bindingPanel.popapEdit.visibility = View.GONE
                        bindingPanel.gallery.visibility = View.VISIBLE
                        bindingPanel.send.visibility = View.VISIBLE
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

    private fun setMessage(isOut: Boolean, text: String): Message {
        return Message(0, isOut, text)
    }

    companion object {
        val mTag = arrayOf(BotJob.TAG_MENU, BotJob.TAG_CALC, BotJob.TAG_JOKES)
    }
}