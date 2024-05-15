package com.example.appcrud

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.appcrud.databinding.ActivityGaleriaAppBinding
import com.example.appcrud.databinding.ActivityMainBinding
import com.example.appcrud.databinding.ActivityMenuCameraBinding

class GaleriaApp : AppCompatActivity() {
    private lateinit var binding: ActivityGaleriaAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGaleriaAppBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.rvImages.apply {
            layoutManager = GridLayoutManager(this@GaleriaApp, 2)
            adapter = ImageAdapter(mutableListOf(), this@GaleriaApp)
        }
    }
}