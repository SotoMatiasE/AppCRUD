package com.example.appcrud

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.appcrud.databinding.ActivityGaleriaAppBinding
import com.example.appcrud.databinding.ActivityPdfViewsBinding
import java.io.File

class PdfViews : AppCompatActivity() {

    private lateinit var binding: ActivityPdfViewsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        //ruta donde se encuentra el archivo
        val file = File(Environment.getExternalStorageDirectory(), getString(R.string.name_file_pdf))

        binding.viewPDF.fromFile(file)
        binding.viewPDF.isZoomEnabled =  true

        binding.viewPDF.show()
    }

}