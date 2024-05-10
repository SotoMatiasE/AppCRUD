package com.example.appcrud

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, Constants.DATABASE_NAME,
                                                  null, Constants.DATABASE_VERSION)
{
    override fun onCreate(db: SQLiteDatabase?) {
        //DEFINIR LA ESTRUCTURA DE LA DB Y DEFINIR LAS TABLAS QUE ESTAN RELACIUONANDO E INTERACTUANDO
        val createTable = "CREATE TABLE ${Constants.ENTITY_APP} (" +
                "${Constants.PROPERTY_ID} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${Constants.PROPERTY_NAME} STRING(60), ${Constants.PORPERTY_PASSWORD} CHAR," +
                "${Constants.PROPERTY_IS_FINISHED} BOOLEAN)" //creamos la db con OBJ Note
        db?.execSQL(createTable) //le decimo que se ejecute

        val table2 = "CREATE TABLE ${Constants.ENTITY_PRODUCT}," +
                "${Constants.PROPERTY_IDPROD} INTEGER PRIMARY KEY AUTOINCREMENT," +
                "${Constants.PROPERTY_PRODUCT} STRING(60), ${Constants.PROPERTY_PRICE} INTEGER)"
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //METODO ES PARA ALTERAR LA DB
    }

    /*fun getAllProducts(): MutableList<Product> {
        //METODO PARA OBTENER TODOS LOS PRODUCTOS DE LA DB
        val prods: MutableList<Product> = mutableListOf()

        val database = this.readableDatabase
        val query = "SELECT * FROM ${Constants.ENTITY_APP}"

        val result = database.rawQuery(query, null)

        if (result.moveToFirst()){
            *//*do {
                val idProd = result.getColumnIndex(Constants.PROPERTY_ID)
                //val product
            }*//*
        }
    }*/
}
