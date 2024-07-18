package com.example.productapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.databinding.ActivityLoginBinding



class LoginActivity : AppCompatActivity() {
        private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initUI()

    }

    private fun initUI() {
        binding.Login.setOnClickListener {
            val nombre = binding.aceUsuario.text.toString()
            if (nombre.isNotEmpty()) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("Extra", nombre)
                startActivity(intent)
            }
        }
    }
}