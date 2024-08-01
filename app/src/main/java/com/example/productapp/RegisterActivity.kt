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
    private var colectionName:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth
        db = FirebaseFirestore.getInstance()


        initUI()
        Log.i("Login", "entro en EL REGISTER")

    }

    private fun initUI() {
        binding.btnBack.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnAdd.setOnClickListener {
            val email = binding.registerEmail.text.toString()
            val password = binding.registerContraseA.text.toString()
            val confirm = binding.registerConfirmarContraseA.text.toString()
            if (email.isEmpty()){
                Toast.makeText(applicationContext, "Introduzca un correo", Toast.LENGTH_SHORT).show()

            }else if (password.isEmpty()) {
            Toast.makeText(applicationContext, "Introduzca contraseña", Toast.LENGTH_SHORT).show()
            }else if (confirm.isEmpty()) {
            Toast.makeText(applicationContext, "Confirme la contraseña", Toast.LENGTH_SHORT).show()
            }else if (password != confirm) {
            Toast.makeText(applicationContext, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            }else{
                createAccount(email, password)
                Log.i("Login", "NO esta bacio, el correo es ${email}")
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
                    colectionName = email
                    updateUI(user)
                    Toast.makeText(applicationContext, "Se agrego el usuario correctamente", Toast.LENGTH_SHORT).show()
                    Log.i("Login", "se agrego el usuario nashe ${user}")
                    Log.i("Login", "muestra coleccionName ${colectionName}")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                    Log.i("Login", "No se agrego el usuario")
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    updateUI(null)

                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if(user != null){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("EmailColeccion", colectionName)
        startActivity(intent)
        }else{
            Toast.makeText(applicationContext, "No se pudo agregar el usuario correctamente", Toast.LENGTH_SHORT).show()
        }
    }

}
