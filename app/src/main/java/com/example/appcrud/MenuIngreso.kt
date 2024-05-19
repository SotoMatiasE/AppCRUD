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
import android.content.ActivityNotFoundException
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.text.TextPaint
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
                    101
                )
            } else {
                // Iniciar la actividad de la cámara
                val intent = Intent(this, MenuCamera::class.java)
                editResult.launch(intent)
            }
        }

        binding.btnApi.setOnClickListener {
            val intent = Intent(this, ApiView::class.java)
            startActivity(intent)
        }

        val buttonCreatePdf = findViewById<Button>(R.id.btn_create_pdf)
        buttonCreatePdf.setOnClickListener {
            createPdfFromRecyclerView()
        }

        val btnPdf = findViewById<Button>(R.id.btn_create_pdf)
        btnPdf.setOnClickListener {
            createPdfFromRecyclerView()

            if (checkPermissions()) {
                navigateToMenuIngreso()
            } else {
                requestPermissions()
            }
        }

    }

    private fun checkPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Environment.isExternalStorageManager()
        } else {
            val writePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val readPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:$packageName")
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            } catch (e: Exception) {
                val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                startActivityForResult(intent, PERMISSION_REQUEST_CODE)
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                navigateToMenuIngreso()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMenuIngreso() {
        val intent = Intent(this, MenuIngreso::class.java)
        startActivity(intent)
    }


    private fun createPdfFromRecyclerView() {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        paint.textSize = 12f

        var yPosition = 30f

        val productList = obtenerListaDeProductos()
        productList.forEach { product ->
            canvas.drawText("${product.productName} - ${if (product.isFinished) "Finished" else "Not Finished"}", 10f, yPosition, paint)
            yPosition += 20f
        }

        pdfDocument.finishPage(page)

        val directoryPath = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.absolutePath
        if (directoryPath != null) {
            val file = File(directoryPath, "productos.pdf")
            try {
                pdfDocument.writeTo(FileOutputStream(file))
                Toast.makeText(this, "PDF generado con éxito", Toast.LENGTH_SHORT).show()
                openPdfFileAutomatically(file) // Cambio aquí
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error al generar el PDF", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No se pudo acceder al directorio de documentos", Toast.LENGTH_SHORT).show()
        }
        pdfDocument.close()
    }

    private fun openPdfFileAutomatically(pdfFile: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", pdfFile)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // Necesario para Android 7.0 y superior
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No hay visor de PDF instalado", Toast.LENGTH_SHORT).show()
        }
    }

    private fun obtenerListaDeProductos(): List<Product> {
        return prodAdapter.getAllProducts()
    }

    private fun openPdfFile(pdfFile: File) {
        val uri = Uri.fromFile(pdfFile)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No hay visor de PDF instalado", Toast.LENGTH_SHORT).show()
        }
    }

    /*private fun drawProductsList(canvas: Canvas, textPaint: TextPaint, productList: List<Product>) {
        val startY = 200f // Posición inicial en el eje Y
        var currentY = startY // Variable para realizar un seguimiento de la posición actual en el eje Y

        // Iterar sobre la lista de productos
        for (product in productList) {
            // Concatenar el nombre del producto y su estado (finalizado o no finalizado)
            val productText = "${product.productName} - ${if (product.isFinished) "Finished" else "Not Finished"}"

            // Dibujar el texto del producto en el lienzo en la posición actual
            canvas.drawText(productText, 10f, currentY, textPaint)

            // Incrementar la posición actual en el eje Y para el siguiente producto
            currentY += 50f // Incrementa la posición en 50 píxeles (puedes ajustar este valor según sea necesario)
        }
    }
    */

    private fun savePdfToFile(pdfDocument: PdfDocument): File? {
        val docFolder = File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), getString(R.string.name_file_pdf))
        if (!docFolder.exists()){
            docFolder.mkdir()
        }

        val pdfFile = File(docFolder.absoluteFile, "Archivo.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(pdfFile))
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun onResume() {
        super.onResume()
        //checkPermissionsAndActions()
    }

    // Verificar permisos antes de generar el PDF y abrir la cámara


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
                getData()
            }
        }
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    navigateToMenuIngreso()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
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

    companion object
    const val PERMISSION_REQUEST_CODE = 100
}