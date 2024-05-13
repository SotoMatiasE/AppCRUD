package com.example.appcrud

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcrud.Constants.REQUEST_IMAGE_CAPTURE
import com.example.appcrud.databinding.ActivityMenuIngresoBinding
import com.google.android.material.snackbar.Snackbar

class MenuIngreso : AppCompatActivity() {
    private lateinit var binding: ActivityMenuIngresoBinding
    private lateinit var prodAdapter: ProductAdapter
    private lateinit var prodFinishAdapter: ProductAdapter
    private lateinit var database: DatabaseHelper
    private lateinit var imageUri: Uri
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuIngresoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = DatabaseHelper(this)//INSTANCIAMOS LA CLASE DataBaseHelper


        prodAdapter = ProductAdapter(mutableListOf(), this)
        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@MenuIngreso)
            adapter = prodAdapter
        }

        prodFinishAdapter = ProductAdapter(mutableListOf(), this)
        binding.rvNotesFinished.apply {
            layoutManager = LinearLayoutManager(this@MenuIngreso)
            adapter = prodFinishAdapter
        }

        //Click open camera
        binding.btnPhoto.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE)
        }

        binding.btnAdd.setOnClickListener {
            if (binding.etDescription.text.toString().isNotBlank()) {
                val prod = Product(productName = binding.etDescription.text.toString().trim())
                prod.idProduct = database.insertProd(prod)

                if (prod.idProduct != Constants.ID_ERROR.toLong()) {
                    addProdAutm(prod)
                    binding.etDescription.text?.clear()
                    showMessage(R.string.msg_operation_sucess)
                }else {
                    showMessage(R.string.msg_operation_error)
                }
            }else{
                binding.etDescription.error = getString(R.string.validation_field_require)
            }
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Aquí puedes hacer algo con la imagen capturada, como almacenarla en la base de datos.
            // Por ejemplo, puedes convertir el bitmap a un Uri y guardarlo en la base de datos.
            //val imageUri = saveImageToDatabase(imageBitmap)
            // También puedes mostrar la imagen en una vista previa si lo deseas.
            //binding.imageView.setImageBitmap(imageBitmap)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_photo, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onStart() {
        super.onStart()
        getData()
    }

    private fun getData() {
        val data = database.getAllProducts()
        data.forEach { prod ->
            addProdAutm(prod)
        }
    }

    private fun addProdAutm(prod: Product) {
        if (prod.isFinished) {
            prodFinishAdapter.add(prod)
        } else {
            prodAdapter.add(prod)
        }
    }

    private fun deleteProdAutom(prod: Product) {
        if (prod.isFinished) {
            prodAdapter.remove(prod)
        } else {
            prodFinishAdapter.remove(prod)
        }
    }

    fun onChecked(prod: Product) {
        if (database.updateProd(prod)){
            deleteProdAutom(prod)
            addProdAutm(prod)
        }else {
            showMessage(R.string.msg_operation_error)
        }
    }

    fun onLongClick(product: Product, currentAdapter: ProductAdapter) {
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

    private fun showMessage(msgRes: Int) {
        Snackbar.make(binding.root, getString(msgRes), Snackbar.LENGTH_SHORT).show()

    }
}