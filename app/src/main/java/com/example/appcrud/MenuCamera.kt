package com.example.appcrud

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.appcrud.databinding.ActivityMenuCameraBinding
import com.google.android.material.snackbar.Snackbar

class MenuCamera : AppCompatActivity() {
    private lateinit var binding: ActivityMenuCameraBinding
    private var imageUri: Uri? = null
    private lateinit var img: ImageView
    private lateinit var database: DatabaseHelper
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuCameraBinding.inflate(layoutInflater)
        layout = binding.main
        setContentView(binding.root)

        //ESTA OPCION APLICA POR DEFAULT LA FLECHA SUPERIOR PARA VOLVER AL MENU ANTERIOR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        database = DatabaseHelper(this)//INSTANCIAMOS LA CLASE DataBaseHelper

        img = findViewById(R.id.ivPhoto)

        binding.btnSaveImg.setOnClickListener {
            if (imageUri != null) {
                val image = Image(image = imageUri.toString())
                val insertedId = database.insertImage(image)
                if (insertedId != -1L) {
                    // La imagen se guardó correctamente en la base de datos
                    Snackbar.make(layout, "Image saved successfully", Snackbar.LENGTH_SHORT).show()
                    // Restablecer el ImageView o limpiar la URI de la imagen
                } else {
                    Snackbar.make(layout, "Failed to save image", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                Snackbar.make(layout, "No image to save", Snackbar.LENGTH_SHORT).show()
            }
        }

        binding.btnCam.setOnClickListener {
            /*if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    100
                )
            } else {*/
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager)!= null) {
                    startActivityForResult(intent, 100)
                }
            }

    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_save_photo, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun sendData() {
        val intent = Intent()

        //VALIDACION DE CAMPOS
        with(binding) {
            intent.apply {
                putExtra(getString(R.string.key_cam), imageUri.toString())
            }
        }
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                img.setImageBitmap(imageBitmap)
                // Aquí podrías guardar la imagen en una variable o en el sistema de archivos si es necesario
            }
        }
    }

}
