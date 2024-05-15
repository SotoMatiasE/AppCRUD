package com.example.appcrud


import android.os.Bundle
import android.os.Environment
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.appcrud.databinding.ActivityPdfViewsBinding
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

        binding.viewPDF.fromFile(file)
        binding.viewPDF.isZoomEnabled =  true

        binding.viewPDF.show()
    }

}