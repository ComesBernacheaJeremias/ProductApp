package com.example.productapp

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.databinding.ActivityRegisterBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()


        initUI()
        Log.i("Login", "entro en EL REGISTER")

    }

    private fun initUI() {
        Log.i("Login", "entro en EL REGISTER 222")


        binding.btnAdd.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerContraseA.text.toString()
            val confirm = binding.registerConfirmarContraseA.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                createAccount(email, password)
                Log.i("Login", "NO esta bacio")
            }else{
                Toast.makeText(applicationContext, "Introduzca correo y contraseña", Toast.LENGTH_SHORT).show()
                Log.i("Login", "Introducir Correo y Contraseña")
            }

        }
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(ContentValues.TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    updateUI(user)
                    Toast.makeText(applicationContext, "Se agrego el usuario correctamente", Toast.LENGTH_SHORT).show()
                    Log.i("Login", "se agrego el usuario nashe")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Log.i("Login", "se agrego el usuario nashe")
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                    Toast.makeText(applicationContext, "No se pudo agregar el usuario correctamente", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

}
