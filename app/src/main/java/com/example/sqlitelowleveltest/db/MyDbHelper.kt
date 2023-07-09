package com.example.sqlitelowleveltest.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

//класс нужен для создания база данных и таблицы в ней

class MyDbHelper(context: Context) : SQLiteOpenHelper(context, MyDbNameClass.DATABASE_NAME , null, MyDbNameClass.DATABASE_VERSION) {  //создание БД
    override fun onCreate(p0: SQLiteDatabase?) {

       p0?.execSQL(MyDbNameClass.CREATE_TABLE)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {  //обновление БД
        p0?.execSQL(MyDbNameClass.SQL_DELETE_TABLE)
        onCreate(p0)
    }
}