package com.example.productapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.databinding.ActivityAllDocumentBinding


class AllDocumentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllDocumentBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val allProductos = binding.tvAllProductos
        val productos = intent.extras?.getString("Extra").orEmpty()
        allProductos.text = productos

    }


}
