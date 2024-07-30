package com.example.productapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.databinding.ActivityLoginBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore


class LoginActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private var Email: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()




        initUI()

    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            reload()
        }
    }

    private fun initUI() {
        binding.Login.setOnClickListener {
            val email = binding.correo.text.toString()
            val password = binding.contraseA.text.toString()

            if (email.isEmpty()) {
                Toast.makeText(applicationContext, "Introduzca un correo", Toast.LENGTH_SHORT)
                    .show()

            } else if (password.isEmpty()) {
                Toast.makeText(applicationContext, "Introduzca una contraseña", Toast.LENGTH_SHORT)
                    .show()
            } else {
                signIn(email, password)
                Log.i("Login", "NO esta bacio, el correo es ${email}")
            }


        }
        binding.Registrar.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            Log.i("Login", "se apreto el boton Registrar")
        }
    }


    //Acceso usuario existente
    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        Log.i("Login", "Entro en la funcion singnIn, veremos")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    Email = email
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)
                }
            }
        // [END sign_in_with_email]
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("LoginEmailColeccion", Email)
            startActivity(intent)
        } else {
            Toast.makeText(
                baseContext,
                "No existe el Usuario.",
                Toast.LENGTH_SHORT,
            ).show()
        }
    }

    private fun reload() {

    }


}