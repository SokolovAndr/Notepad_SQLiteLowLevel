package com.example.sqlitelowleveltest.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//!!! весь этот класс нуженн для того, чтобы нам не писать каждый раз кучу кода в основной активити для открытия, обновления и закрытия базы данных

class MyDbManager(context : Context) {
    val myDbHelper = MyDbHelper(context)
    var db: SQLiteDatabase? = null

    fun openDb(){   //откртыие БД
        db = myDbHelper.writableDatabase
    }

    suspend fun insertToDb(title: String,content: String, uri: String, time:String) = withContext(Dispatchers.IO) {     //запись в БД
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.insert(MyDbNameClass.TABLE_NAME, null, values)
    }

    suspend fun updateItem(title: String,content: String, uri: String, id:Int, time:String) = withContext(Dispatchers.IO){     //обновление записи в БД
        val selection = BaseColumns._ID + "=$id"
        val values = ContentValues().apply {
            put(MyDbNameClass.COLUMN_NAME_TITLE, title)
            put(MyDbNameClass.COLUMN_NAME_CONTENT, content)
            put(MyDbNameClass.COLUMN_NAME_IMAGE_URI, uri)
            put(MyDbNameClass.COLUMN_NAME_TIME, time)
        }
        db?.update(MyDbNameClass.TABLE_NAME, values, selection, null)
    }

    fun removeItemFromDb(id: String) {     //удаление из БД по id
        val selection = BaseColumns._ID + "=$id"
        db?.delete(MyDbNameClass.TABLE_NAME, selection, null)
    }

    suspend fun readDbData(searchText:String): ArrayList<ListItem> = withContext(Dispatchers.IO){    //suspend гооворит о том, что эта функция будет блокировать корутину, а withContext(Dispatchers.IO) говорит о том, что код запуститься во второпстпенном потоке
        val dataList = ArrayList<ListItem>() //массив для возврата
        val selection = "${MyDbNameClass.COLUMN_NAME_TITLE} like ?"   //это запрос к SQLite
        val cursor = db?.query(MyDbNameClass.TABLE_NAME, null,selection, arrayOf("%$searchText%"),   //"%$searchText%" - значит что будет искать не по полному совпадению
            null,null,null)  //val cursor взят ио офиц. документации

        while (cursor?.moveToNext()!!){
            val dataTitle = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TITLE)) //в уроке getColumnIndex()
            val dataContent = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_CONTENT)) //в уроке getColumnIndex()
            val dataUri = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_IMAGE_URI)) //в уроке getColumnIndex()
            val dataId = cursor.getInt(cursor.getColumnIndexOrThrow(BaseColumns._ID))
            val time = cursor.getString(cursor.getColumnIndexOrThrow(MyDbNameClass.COLUMN_NAME_TIME))
            val item = ListItem()
            item.title = dataTitle
            item.desc = dataContent
            item.uri = dataUri
            item.id = dataId
            item.time= time
            dataList.add(item)
        }
        cursor.close()
        return@withContext dataList
    }

    fun closeDb (){
        myDbHelper.close()
    }

}