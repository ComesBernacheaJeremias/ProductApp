package com.example.productapp

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.productapp.databinding.ActivityMainBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FieldValue.delete
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
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
        val etCodigo = binding.etCodigo
        val etPrecio = binding.etPrecio
        etPrecio.inputType = InputType.TYPE_CLASS_NUMBER
        val etProducto = binding.etProducto
        val etDescription = binding.etDescription

        val user = hashMapOf(
            "Codigo" to etCodigo.text.toString(),
            "Producto" to etProducto.text.toString(),
            "Precio" to etPrecio.text.toString().toDouble(),
            "Descripcion" to etDescription.text.toString()

        )


        Log.i("corcho", "Agrega un algo. se apreto el boton")
        db.collection("cities").document("${etCodigo.text}")
            .set(user)
            .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w(TAG, "Error writing document", e) }

    }

    @SuppressLint("SetTextI18n")
    private fun getData() {
        val etCodigo = binding.etCodigo
        val docRef = db.collection("cities").document("${etCodigo.text}")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                val data = document.data
                val formattedData = StringBuilder()
                for ((key, value) in data!!) {
                    formattedData.append("$key: $value\n")
                }
                Log.i("corchometro", "DocumentSnapshot data: ${document.data}")
                binding.tvSearch.text = formattedData.toString()
            } else {
                binding.tvSearch.text = "No data found"
            }

        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error deleting document", exception)
            binding.tvSearch.text = "No se encuentra el codigo"
        }
    }


    private fun updateData(documentId: String) {
        val etCodigo = binding.etCodigo
        val update = binding.etPrecio
        update.inputType = InputType.TYPE_CLASS_NUMBER
        val precioUpdate = update.text.toString().toDoubleOrNull()
        val documentRef = db.collection("cities").document("${etCodigo.text}")

        Log.i("corcho", "ACTUALIZAR se apreto el boton")

        //control de errores
        if (etCodigo.text.isNullOrEmpty()) {
            binding.tvSearch.text = "Por favor ingrese un código válido"
            return
        }
        if (update.text.isNullOrEmpty()) {
            binding.tvSearch.text = "Por favor ingrese un porcentaje válido"
            return
        }
        if (precioUpdate == null) {
            binding.tvSearch.text = "Por favor ingrese un porcentaje válido"
            return
        }




        documentRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val precioActual = document.getDouble("Precio")
                    if (precioActual != null) {
                        // Calcular el nuevo precio sumando el porcentaje
                        val incremento = precioActual * (precioUpdate / 100)
                        val nuevoPrecio = precioActual + incremento

                        // Actualizar el campo "Precio" con el nuevo valor
                        documentRef.update("Precio", nuevoPrecio)

                            .addOnSuccessListener {
                                Log.d(TAG, "DocumentSnapshot successfully updated!")
                                Log.i(
                                    "corchometro",
                                    "quiero que muestre ${update.text} que es el precio y ${nuevoPrecio}"
                                )
                                binding.tvSearch.text = "Precios Actualizados"
                            } .addOnFailureListener { e ->
                                Log.w(TAG, "Error updating document", e)
                                binding.tvSearch.text = "Error al actualizar el precio"}

                    }else {
                        binding.tvSearch.text = "Precio actual no encontrado"
                    }
                }else {
                    binding.tvSearch.text = "No data found"
                }
            }.addOnFailureListener { exception ->
                Log.w(TAG, "Error updating document", exception)
                binding.tvSearch.text = "Error al obtener el documento"
            }
    }


    private fun deleteData(documentId: String) {
        val etCodigo = binding.etCodigo
        Log.i("corcho", "AELIMINAR. se apreto el boton")
        db.collection("cities").document("${etCodigo.text}")
            .delete()
            .addOnSuccessListener {
                Log.i("corchometro", "DocumentSnapshot successfully deleted! SI Se borr")


            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error deleting document", e)
            }
    }
}

