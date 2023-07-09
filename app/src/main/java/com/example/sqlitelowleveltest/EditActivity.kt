package com.example.sqlitelowleveltest

import android.app.Activity
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.sqlitelowleveltest.db.MyDbHelper
import com.example.sqlitelowleveltest.db.MyDbManager
import com.example.sqlitelowleveltest.db.MyIntentConstants
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class EditActivity : AppCompatActivity() {

    var id = 0
    var isEditState = false  //булеове значение редактируем или просто просматриваем заметку
    val imageRequestCode = 10
    var tempImageUri = "empty"  //Переменная для хранения пути. если пользователь не выбрал картинку, то за место пути к картинке запишется слово empty
    val myDbManager = MyDbManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_activity)
        getMyIntents()
    }

    override fun onDestroy() { //когда активити закрывается, то закрываем базу данных тоже
        super.onDestroy()
        myDbManager.closeDb()
    }

    override fun onResume() { //когда активити отрывается, то запускаем базу данных
        super.onResume()
        myDbManager.openDb()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {

            var imMainImage = findViewById<ImageView>(R.id.imMainImage)
            imMainImage.setImageURI(data?.data)
            tempImageUri = data?.data.toString()
            contentResolver.takePersistableUriPermission(
                data?.data!!,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )  //дает не временную ссылку на картинку, а постоянную)

        }

    }

    fun onClickAddImage(view: View) {
        var mainImageLayout = findViewById<ConstraintLayout>(R.id.mainImageLayout)
        mainImageLayout.visibility = View.VISIBLE

        var fbAddImage = findViewById<FloatingActionButton>(R.id.fbAddImage)
        fbAddImage.visibility = View.GONE
    }

    fun onClickDeleteImage(view: View) {
        var mainImageLayout = findViewById<ConstraintLayout>(R.id.mainImageLayout)
        mainImageLayout.visibility = View.GONE

        var fbAddImage = findViewById<FloatingActionButton>(R.id.fbAddImage)
        fbAddImage.visibility = View.VISIBLE

        tempImageUri = "empty"
    }

    fun onClickChooseImage(view: View) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)  //должна открыться галерия
        intent.type = "image/*"   //выбираем все картинки
//        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION  //дает не временную ссылку на картинку, а постоянную
        startActivityForResult(intent, imageRequestCode)
    }

    fun onClickSave(view: View) {
        var edTitle = findViewById<TextView>(R.id.edTitle)
        var edDesc = findViewById<TextView>(R.id.edDesc)
        val myTitle = edTitle.text.toString()
        val myDesc = edDesc.text.toString()

        if (myTitle != "" && myDesc != "") {
            CoroutineScope(Dispatchers.Main).launch {
                if (isEditState) {
                    myDbManager.updateItem(myTitle, myDesc, tempImageUri, id, getCurrentTime())
                } else {
                    myDbManager.insertToDb(myTitle, myDesc, tempImageUri, getCurrentTime())
                }
                finish()
            }
        }
    }

    fun getMyIntents() {

        var fbEdit = findViewById<FloatingActionButton>(R.id.fbEdit)
        fbEdit.visibility = View.GONE

        var i = intent

        if (i != null) {

            if (i.getStringExtra(MyIntentConstants.I_TITLE_KEY) != null) {

                var fbAddImage = findViewById<FloatingActionButton>(R.id.fbAddImage)
                fbAddImage.visibility = View.GONE

                var edTitle = findViewById<TextView>(R.id.edTitle)
                var edDesc = findViewById<TextView>(R.id.edDesc)

                edTitle.setText(i.getStringExtra(MyIntentConstants.I_TITLE_KEY))
                isEditState = true
                edTitle.isEnabled = false
                edDesc.isEnabled = false

                //var fbEdit = findViewById<FloatingActionButton>(R.id.fbEdit)
                fbEdit.visibility = View.VISIBLE

                edDesc.setText(i.getStringExtra(MyIntentConstants.I_DESC_KEY))
                id = i.getIntExtra(MyIntentConstants.I_ID_KEY, 0)
                if (i.getStringExtra(MyIntentConstants.I_URI_KEY) != "empty") {

                    var mainImageLayout = findViewById<ConstraintLayout>(R.id.mainImageLayout)
                    mainImageLayout.visibility = View.VISIBLE

                    tempImageUri = i.getStringExtra(MyIntentConstants.I_URI_KEY)!!

                    var imMainImage = findViewById<ImageView>(R.id.imMainImage)
                    imMainImage.setImageURI(Uri.parse(tempImageUri))

                    var imButtonDeleteImage = findViewById<ImageButton>(R.id.imButtonDeleteImage)
                    imButtonDeleteImage.visibility = View.GONE

                    var imButtonEditImage = findViewById<ImageButton>(R.id.imButtonEditImage)
                    imButtonEditImage.visibility = View.GONE

                }
            }
        }

    }

    fun onEditEnable(view: View) {
        var edTitle = findViewById<TextView>(R.id.edTitle)
        var edDesc = findViewById<TextView>(R.id.edDesc)


        edTitle.isEnabled = true
        edDesc.isEnabled = true

        var fbEdit = findViewById<FloatingActionButton>(R.id.fbEdit)
        fbEdit.visibility = View.GONE

        var fbAddImage = findViewById<FloatingActionButton>(R.id.fbAddImage)
        fbAddImage.visibility = View.VISIBLE

        if (tempImageUri == "empty") return

        var imButtonEditImage = findViewById<ImageButton>(R.id.imButtonEditImage)
        imButtonEditImage.visibility = View.VISIBLE

        var imButtonDeleteImage = findViewById<ImageButton>(R.id.imButtonDeleteImage)
        imButtonDeleteImage.visibility = View.VISIBLE

    }

    private  fun getCurrentTime():String{
        val time = Calendar.getInstance().time
        val formatter = SimpleDateFormat("dd-MM-yy kk:mm", Locale.getDefault())  //делате красивую дату
        return formatter.format(time)
    }

}