package com.example.appcrud

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcrud.databinding.ActivityMenuIngresoBinding
import com.google.android.material.snackbar.Snackbar

class MenuIngreso : AppCompatActivity() {
    private lateinit var binding: ActivityMenuIngresoBinding
    private lateinit var prodAdapter: ProductAdapter
    private lateinit var prodFinishAdapter: ProductAdapter
    private lateinit var database: DatabaseHelper
    private lateinit var imageUri: Uri

    private val editResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            imageUri = Uri.parse(it.data?.getStringExtra(getString(R.string.key_cam)))
        }
    }
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

        binding.btnAdd.setOnClickListener {
            if (binding.etDescription.text.toString().isNotBlank()) {
                val prod = Product(
                    productName = binding.etDescription.text.toString().trim()
                )
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

        binding.btnPhoto.setOnClickListener {
            val intent = Intent(this, MenuCamera::class.java)

            startActivity(intent)
            
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    100
                )
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.action_camera -> {
                val intent = Intent(this, MenuCamera::class.java)
                intent.putExtra(getString(R.string.key_cam), imageUri.toString())

                editResult.launch(intent)
            }
        }
        return super.onOptionsItemSelected(item)
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



    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Log.i("Permission: ", "Granted")
            } else {
                Log.i("Permission: ", "Denied")
            }
        }


    fun View.showSnackbar(
        view: View,
        msg: String,
        length: Int,
        actionMessage: CharSequence?,
        action: (View) -> Unit
    ) {
        val snackbar = Snackbar.make(view, msg, length)
        if (actionMessage != null) {
            snackbar.setAction(actionMessage) {
                action(this)
            }.show()
        } else {
            snackbar.show()
        }
    }


}