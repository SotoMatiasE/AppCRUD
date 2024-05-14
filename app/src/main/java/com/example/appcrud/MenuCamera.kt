package com.example.appcrud

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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appcrud.databinding.ActivityMenuCameraBinding
import com.google.android.material.snackbar.Snackbar

class MenuCamera : AppCompatActivity() {
    private lateinit var binding: ActivityMenuCameraBinding
    private var imageUri: Uri? = null
    private lateinit var img: ImageView
    private lateinit var layout: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuCameraBinding.inflate(layoutInflater)
        layout = binding.main
        setContentView(binding.root)

        //ESTA OPCION APLICA POR DEFAULT LA FLECHA SUPERIOR PARA VOLVER AL MENU ANTERIOR
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        img = findViewById(R.id.ivPhoto)

        binding.btnCam.setOnClickListener {
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
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                if (intent.resolveActivity(packageManager)!= null) {
                    startActivityForResult(intent, 100)
                }
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
