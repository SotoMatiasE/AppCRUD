package com.example.appcrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcrud.databinding.ActivityMenuIngresoBinding
import com.google.android.material.snackbar.Snackbar

class MenuIngreso : AppCompatActivity() {
    private lateinit var binding: ActivityMenuIngresoBinding
    private lateinit var prodAdapter: ProductAdapter
    private lateinit var prodFinishAdapter: ProductAdapter
    private lateinit var database: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuIngresoBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        database = DatabaseHelper(this)//INSTANCIAMOS LA CLASE DataBaseHelper


        prodAdapter = ProductAdapter(mutableListOf(), this)
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@MenuIngreso)
            adapter = prodAdapter
        }



        binding.btnAdd.setOnClickListener {
            if (binding.etDescription.text.toString().isNotBlank()) {
                val prod = Product(productName = binding.etDescription.text.toString())
                prod.idProduct = database.insertProd(prod)
                if (prod.idProduct != Constants.ID_ERROR.toLong()) {

                }
            }

            //ingreso menu2
            val btnNext: Button = findViewById(R.id.btnNext)
            btnNext.setOnClickListener {
                val intent: Intent = Intent(this, MenuIngreso::class.java)
                startActivity(intent)
            }


        }
        fun getData() {
            val data = database.getAllProducts()
            data.forEach { prod ->
                addProdAutm(prod)
            }
        }

        fun onStart() {
            super.onStart()
            getData()
        }


        fun addProdAutm(prod: Product) {
            if (prod.isFinished) {
                prodFinishAdapter.add(prod)
            } else {
                prodAdapter.add(prod)
            }
        }




        fun deleteProdAutom(prod: Product) {
            if (prod.isFinished) {
                prodAdapter.remove(prod)
            } else {
                prodFinishAdapter.remove(prod)
            }
        }

        override fun onChecked(product: Product, appAdapter: ProductAdapter) {
            if (database.updateProd(product)){
                deleteProdAutom(product)
                addProdAutm(product)
            }else {
                showMessage(R.string.message_db_error)
            }
        }

        override fun onLongClick(product: Product, currentAdapter: ProductAdapter) {
            val builder = AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_title))
                .setPositiveButton(getString(R.string.dialog_ok)) { _, _ ->
                    if (database.deleteProd(product)) {
                        currentAdapter.remove(product)
                        showMessage(R.string.msg_operation_sucess)
                    } else {
                        showMessage(R.string.message_db_error)
                    }
                }
                .setNegativeButton(getString(R.string.dialog_cancel), null)

            builder.create().show()
        }



    }

    private fun showMessage(msgRes: Int) {
        Snackbar.make(binding.root, getString(msgRes), Snackbar.LENGTH_SHORT).show()

    }
}