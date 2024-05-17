package com.example.appcrud

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcrud.databinding.ActivityApiViewBinding
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiView : AppCompatActivity() {

    private lateinit var binding: ActivityApiViewBinding

    val urlBase = "https://jsonplaceholder.typicode.com/" //colocar en constante
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityApiViewBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val retrofit = Retrofit.Builder() //en obj
            .baseUrl(urlBase)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        //llamado a la corrutina
        val service = retrofit.create(PostApiService::class.java)
        lifecycleScope.launch {
            val response = service.getUserPost()
            response.forEach{
                println(it)
            }

            runOnUiThread {
                val textView = findViewById<TextView>(R.id.tvApi)
               textView.text = response.first().title //pone el primer valor que tira retrofit
            }
        }
    }


}