package com.example.appcrud

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appcrud.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity(), OnClickListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prodAdapter: ProductAdapter
    private lateinit var prodFinishAdapter: ProductAdapter
    private lateinit var database: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        /*
        database = DatabaseHelper(this)//INSTANCIAMOS LA CLASE DataBaseHelper*/
    }

    override fun onChecked(prod: Product, appAdapter: ProductAdapter) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(prod: Product, currentAdapter: ProductAdapter) {
        TODO("Not yet implemented")
    }


}
