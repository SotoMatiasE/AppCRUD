package com.example.appcrud

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, Contants.DATABASE_NAME,
                                                  null, Contants.DATABASE_VERSION)
{
    override fun onCreate(db: SQLiteDatabase?) {
        //DEFINIR LA ESTRUCTURA DE LA DB Y DEFINIR LAS TABLAS QUE ESTAN RELACIUONANDO E INTERACTUANDO
        /*val createTable = "CREATE TABLE ${Constants.TABLE_NAME}"*/

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}
