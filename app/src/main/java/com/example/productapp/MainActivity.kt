package com.example.productapp

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.FieldValue.delete
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.toObject
import java.nio.file.Files.delete


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
        val etCodigo = binding.etPrecio

        val user = hashMapOf(
            "nombre" to etProducto.text.toString(),
            "codigo" to etCodigo.text.toString(),
            "mas" to "La descripcion del producto"
        )

        //"last" to "Lovelace",
        // "born" to 1815

        Log.i("corcho", "Agrega un algo. se apreto el boton")
        db.collection("cities").document("${etProducto.text}")
            .set(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

        // db.collection("users").document("codigo").add(user)
        //probar que el documento id es el documento del firebase
    }

    private fun getData() {
        val etProducto = binding.etProducto
        val docRef = db.collection("cities").document("${etProducto.text}")
        docRef.get().addOnSuccessListener { document ->
            val data = document.data
            val formattedData = StringBuilder()
            for ((key, value) in data!!) {
                formattedData.append("$key: $value\n")
            }
            Log.i("corchometro", "DocumentSnapshot data: ${document.data}")
            binding.tvSearch.text = formattedData.toString()

        }
    }


    private fun updateData(documentId: String) {
        var producto = binding.etProducto
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
        val etProducto = binding.etProducto
        Log.i("corcho", "AELIMINAR. se apreto el boton")
        db.collection("cities").document("${etProducto.text}")
            .delete()
            .addOnSuccessListener {
                Log.i("corchometro", "DocumentSnapshot successfully deleted! SI Se borr")


            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }
}

