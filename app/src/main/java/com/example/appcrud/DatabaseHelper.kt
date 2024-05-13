package com.example.appcrud

import android.content.ContentValues
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

    fun getAllProducts(): MutableList<Product> {
        //METODO PARA OBTENER TODOS LOS PRODUCTOS DE LA DB
        val prods: MutableList<Product> = mutableListOf()

        val database = this.readableDatabase
        val query = "SELECT * FROM ${Constants.ENTITY_APP}"

        val result = database.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                val idProd = result.getColumnIndex(Constants.PROPERTY_ID)
                val product = result.getColumnIndex(Constants.PROPERTY_PRODUCT)
                val isFinished = result.getColumnIndex(Constants.PROPERTY_IS_FINISHED)
                val prod = Product()
                prod.idProduct = result.getLong(if (idProd >= 0) idProd else 0)
                prod.productName = result.getString(if (product >= 0) product else 0)
                prod.isFinished =
                    result.getInt(if (isFinished >= 0) isFinished else 0) == Constants.TRUE

                prods.add(prod)
            }while (result.moveToNext())
        }
        result.close()//se agrega para que database.rawQuery(query, null) no marque eror
        return prods
    }


    fun insertProd (prod: Product): Long{
        val database = this.writableDatabase //writableDatabase abre la escritura de la DB
        val contentValues = ContentValues().apply {
            put(Constants.PROPERTY_PRODUCT, prod.productName)
            put(Constants.PROPERTY_IS_FINISHED, prod.isFinished)
        }
        //insertar en la tabla almacenado en la variable result
        val resultId = database.insert(Constants.ENTITY_PRODUCT, null, contentValues)

        return resultId
    }

    fun updateProd (prod: Product): Boolean{
        val database = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(Constants.PROPERTY_PRODUCT, prod.productName)
            put(Constants.PROPERTY_IS_FINISHED, prod.isFinished)
        }
        val result = database.update(
            Constants.ENTITY_PRODUCT,
            contentValues,
            "${Constants.PROPERTY_IDPROD} = ${prod.idProduct}",
            null)

        return  result == Constants.TRUE
    }

    fun deleteProd (prod: Product): Boolean{
        val database = this.writableDatabase
        val result = database.delete(
            Constants.ENTITY_PRODUCT,
            "${Constants.PROPERTY_IDPROD}," +
                    "${prod.idProduct}", null )

        return  result == Constants.TRUE
    }
}
