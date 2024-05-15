package com.example.appcrud

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.Manifest
import android.app.Activity
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.text.TextPaint
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appcrud.databinding.ActivityMenuIngresoBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream

class MenuIngreso : AppCompatActivity() {
    private lateinit var binding: ActivityMenuIngresoBinding
    private lateinit var prodAdapter: ProductAdapter
    private lateinit var prodFinishAdapter: ProductAdapter
    private lateinit var layout: View
    private lateinit var imageUri: Uri
    private var dataLoaded = false // Bandera para verificar si los datos ya se han cargado
    private lateinit var database: DatabaseHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMenuIngresoBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.main
        setContentView(view)


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
                } else {
                    showMessage(R.string.msg_operation_error)
                }
            } else {
                binding.etDescription.error = getString(R.string.validation_field_require)
            }
        }

        binding.btnPhoto.setOnClickListener {
            // Verificar y solicitar permisos de cámara
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
            } else {
                // Iniciar la actividad de la cámara
                val intent = Intent(this, MenuCamera::class.java)
                editResult.launch(intent)
            }
        }

        //PERMISOS PDF
        if (checkPermission()){
            Toast.makeText(this, getString(R.string.permissions_acepted), Toast.LENGTH_SHORT).show()
        } else {
            requestPermissions()
        }

        binding.btnPdf.setOnClickListener {
            //val intent = Intent(this, PdfViews::class.java)


            generarPdf()
        }
    }

    private fun generarPdf() {
        val pdfDocument = PdfDocument()

        val title = TextPaint()
        val description = TextPaint()
        val pageInfo = PdfDocument.PageInfo.Builder(1000, 1000, 1).create()
        val page1 = pdfDocument.startPage(pageInfo)
        val titleText = getString(R.string.title_prod)
        val canvas = page1.canvas

        title.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        title.textSize = 20f
        canvas.drawText(titleText, 10f, 150f, title)

        description.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        description.textSize = 14f

        val arrDescription = description

        pdfDocument.finishPage(page1)

        //CREACION DEL DOCUMENTO PDF

        val file = File(Environment.getExternalStorageDirectory(), getString(R.string.name_file_pdf))

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(this, getString(R.string.pdf_created), Toast.LENGTH_SHORT).show()
            val intent = Intent(applicationContext, PdfViews::class.java)
            startActivity(intent)
        }catch (e: Exception) {
            e.printStackTrace()
        }

        pdfDocument.close()
    }

    private val editResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            // Obtener la imagen capturada
            imageUri = Uri.parse(result.data?.getStringExtra(getString(R.string.key_cam)))

            // Verificar si la imagenUri no es nula y guardarla en la base de datos
            if (imageUri != null) {
                // Crear la instancia de la imagen con la URI y guardarla en la base de datos
                val image = Image(imageCap = imageUri.toString())
                val insertedId = database.insertImage(image)

                // Verificar si la imagen se guardó correctamente
                if (insertedId != -1L) {
                    // Mostrar un mensaje de éxito
                    Snackbar.make(binding.root, "Image saved successfully", Snackbar.LENGTH_SHORT).show()

                    // Actualizar la lista de productos solo si la imagen se guardó correctamente
                    getData()
                } else {
                    // Mostrar un mensaje de error si la imagen no se guardó correctamente
                    Snackbar.make(binding.root, "Failed to save image", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            if (data?.getBooleanExtra(getString(R.string.key_img), false) == true) {
                // Actualizar la lista de productos
                getData()
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

    private fun launchIntent(intent: Intent){
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(this, getString(R.string.profile_erro_no_resolve), Toast.LENGTH_SHORT).show()
        }
        //ESTO PREGUNTA SI HAY COMPATIBILIDAD CON API SUPERIOR DEL ANDROID 11
    }


    override fun onStart() {
        super.onStart()
        if (!dataLoaded) {
            getData() // Solo cargar los datos si aún no se han cargado
            dataLoaded = true // Establecer la bandera como verdadera después de cargar los datos
        }
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

    fun onClickRequestPermission(view: View) {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_granted),
                    Snackbar.LENGTH_INDEFINITE,
                    null
                ) {}
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                layout.showSnackbar(
                    view,
                    getString(R.string.permission_required),
                    Snackbar.LENGTH_INDEFINITE,
                    getString(R.string.ok)
                ) {
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }

            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.CAMERA
                )
            }
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

    //PERMISOS PARA PDF
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, arrayOf(
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE), 200)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.isNotEmpty()){
                val writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED
                val readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED
                if (writeStorage && readStorage){
                    Toast.makeText(this, getString(R.string.permission_succes), Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this, getString(R.string.permission_deneid), Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val permission1 = ContextCompat.checkSelfPermission(applicationContext,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val permission2 = ContextCompat.checkSelfPermission(applicationContext,
            android.Manifest.permission.READ_EXTERNAL_STORAGE)

        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED

    }
}