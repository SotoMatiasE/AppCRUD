package com.example.appcrud


import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.appcrud.databinding.ActivityPdfViewsBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File

class PdfViews : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //ruta donde se encuentra el archivo
        val file = File(Environment.getExternalStorageDirectory(), getString(R.string.name_file_pdf))
        val products = // Get the list of products from the database or other source

        binding.viewPDF.fromFile(file)
        binding.viewPDF.isZoomEnabled =  true

        binding.viewPDF.show()
    }

}