package com.example.productapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    private lateinit var db: FirebaseFirestore
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firestore
        db = FirebaseFirestore.getInstance()
        initUI()

    }

    private fun initUI() {
        binding.btAdd.setOnClickListener(View.OnClickListener {
            addData()
        })
        binding.btSearch.setOnClickListener(View.OnClickListener {
            getData()
        })
        binding.btUpdate.setOnClickListener(View.OnClickListener {
            updateData("prueba")
        })
        binding.btDelete.setOnClickListener(View.OnClickListener {
            deleteData("prueba")
        })
    }

    private fun addData() {
        val etProducto = binding.etProducto
        val etPrecio = binding.etPrecio
        val user = hashMapOf(
            "nombre" to etProducto.text.toString(),
            "codigo" to etPrecio.text.toString(),
            "mas" to "La descripcion del producto"
            //"last" to "Lovelace",
           // "born" to 1815
        )
        Log.i("corcho", "Agrega un algo. se apreto el boton")

        db.collection("users").document("codigo").add(user)
    }
    private fun getData() {
        Log.i("corcho", "CONSEGUIR. se apreto el boton")
        val stringBuilder = StringBuilder()
        db.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val nombre = document.getString("nombre")
                    stringBuilder.append("$nombre\n")
                    Log.d(TAG, "${document.id} => ${document.data}")
                    binding.tvSearch.text = stringBuilder.toString()
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }
    private fun updateData(documentId: String) {
        Log.i("corcho", "ACTUALIZAR se apreto el boton")
        val userUpdates = hashMapOf<String, Any>(
            "born" to 1816
        )

        db.collection("users").document(documentId)
            .update(userUpdates)
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
            }
    }
    private fun deleteData(documentId: String) {
        Log.i("corcho", "AELIMINAR. se apreto el boton")
        db.collection("users").document(documentId)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }
}
