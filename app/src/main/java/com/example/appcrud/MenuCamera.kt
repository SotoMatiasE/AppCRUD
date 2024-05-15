package com.example.appcrud

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appcrud.databinding.ActivityMenuCameraBinding
import com.google.android.material.snackbar.Snackbar
import java.io.ByteArrayOutputStream

class MenuCamera : AppCompatActivity() {
    private lateinit var binding: ActivityMenuCameraBinding
    private var imageUri: Uri? = null
    private lateinit var img: ImageView
    private lateinit var database: DatabaseHelper
    private lateinit var layout: View
    private lateinit var currentPhotoPath: String // Almacena la ruta de la foto capturada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuCameraBinding.inflate(layoutInflater)
        val view = binding.root
        layout = binding.main
        setContentView(view)

        //ESTA OPCION APLICA POR DEFAULT LA FLECHA SUPERIOR PARA VOLVER AL MENU ANTERIOR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        database = DatabaseHelper(this)//INSTANCIAMOS LA CLASE DataBaseHelper

        img = findViewById(R.id.ivPhoto)

        binding.btnSaveImg.setOnClickListener {
            // Check if an image has been captured
            if (imageUri != null) {
                // Create a new Image object with the image URI
                val image = Image(imageCap = imageUri.toString())

                // Insert the image into the database
                val insertedId = database.insertImage(image)

                // Check if the image was successfully inserted
                if (insertedId != -1L) {
                    // Show a success message
                    Snackbar.make(binding.root, "Image saved successfully", Snackbar.LENGTH_SHORT).show()

                    // Reset the image view or clear the image URI
                } else {
                    // Show an error message
                    Snackbar.make(binding.root, "Failed to save image", Snackbar.LENGTH_SHORT).show()
                }
            } else {
                // Show an error message if no image has been captured
                Snackbar.make(binding.root, "No image to save", Snackbar.LENGTH_SHORT).show()
            }
        }

        /*binding.btnCam.setOnClickListener {
            // Check if the camera permission has been granted
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_DENIED
            ) {
                // Request the camera permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    100
                )
            } else {
                // Open the camera
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, 100)
            }
        }*/
    }

    fun dispatchTakePictureIntent(view: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, Constants.REQUEST_IMAGE_CAPTURE)
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
            R.id.action_camera -> sendData()
            //HACER ESTO ES LO MISMO Y MEJOR QUE EL if QUE ESTA ABAJO
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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            // Muestra la imagen capturada en el ImageView
            binding.ivPhoto.setImageBitmap(imageBitmap)
        }
    }


    // Método para convertir un bitmap en un array de bytes
    private fun getBitmapAsByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

}
