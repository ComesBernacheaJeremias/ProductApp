package com.example.productapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
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
            updateData()

        })
        binding.btDelete.setOnClickListener(View.OnClickListener {
            deleteData()

        })
        binding.btTodos.setOnClickListener(View.OnClickListener { mostrarTodos() })
    }

    private fun mostrarTodos() {
        val tvResults = binding.tvSearch
        val docRef = db.collection("cities")
        docRef.get().addOnSuccessListener { result ->
            val stringBuilder = StringBuilder()
            for (document in result) {
                stringBuilder.append("ID: ${document.id}\n")
                for ((key, value) in document.data) {
                    stringBuilder.append("\t$key: $value\n")
                }
                stringBuilder.append("\n") // Añadir una línea en blanco entre documentos

            }


            tvResults.text = stringBuilder.toString()

        }

            .addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                // Mostrar el mensaje de error en el TextView
                tvResults.text = "Error al obtener documentos"
            }
    }


    private fun addData() {
        val etCodigo = binding.etCodigo
        val etPrecio = binding.etPrecio
        etPrecio.inputType = InputType.TYPE_CLASS_NUMBER
        val etProducto = binding.etProducto
        val etDescription = binding.etDescription


        if (etCodigo.text.isNullOrEmpty() || etPrecio.text.isNullOrEmpty() || etDescription.text.isNullOrEmpty() || etProducto.text.isNullOrEmpty()) {
            binding.tvSearch.text = "Por favor, complete todos los datos."
            return
        }

        val user = hashMapOf(
            "Codigo" to etCodigo.text.toString(),
            "Producto" to etProducto.text.toString(),
            "Precio" to etPrecio.text.toString().toDouble(),
            "Descripcion" to etDescription.text.toString()

        )
        val docRef = db.collection("cities").document("${etCodigo.text}")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                binding.tvSearch.text = "Codigo ya registrado. Pruebe con otro."
            } else {

                Log.i("corcho", "Agrega un algo. se apreto el boton")
                docRef.set(user)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        binding.etDescription.text.clear()
                        binding.etCodigo.text.clear()
                        binding.etPrecio.text.clear()
                        binding.etProducto.text.clear()
                        binding.tvSearch.text = "Se agrego el producto correctamente"
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing document", e)
                        binding.tvSearch.text =
                            "No se pudo agregar el producto. Verifique los datos"
                    }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getData() {
        val etCodigo = binding.etCodigo.text
        if (etCodigo.isNullOrEmpty()) {
            binding.tvSearch.text = "Por favor, ingrese un Codigo."
            return
        }
        val docRef = db.collection("cities").document("${etCodigo}")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                val data = document.data
                val formattedData = StringBuilder()
                for ((key, value) in data!!) {
                    formattedData.append("$key: $value\n")
                }
                Log.i("corchometro", "DocumentSnapshot data: ${document.data}")

                showData(formattedData.toString(), etCodigo.toString())
                binding.tvSearch.text = formattedData.toString()
            } else {
                binding.tvSearch.text = "Codigo NO encontrado. Pruebe con otro."
            }

        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error deleting document", exception)
            binding.tvSearch.text = "No se encuentra el codigo"
        }
    }

    private fun showData(formattedData: String, etCodigo: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add)
        val tvResolt:TextView = dialog.findViewById(R.id.tvResolt)
        val tvResoltTitle:TextView = dialog.findViewById(R.id.tvResoltTitle)
        val btnBack: Button = dialog.findViewById(R.id.btnBack)
        tvResolt.text = formattedData
        tvResoltTitle.text = etCodigo

        dialog.show()
        btnBack.setOnClickListener{dialog.hide()}

    }


    private fun updateData() {
        val etCodigo = binding.etCodigo
        val update = binding.etPrecio
        val precioUpdate = update.text.toString().toDoubleOrNull()
        val documentRef = db.collection("cities")

        Log.i("corcho", "ACTUALIZAR se apreto el boton")

        //control de errores

        if (update.text.isNullOrEmpty()) {
            binding.tvSearch.text = "Por favor ingrese un porcentaje válido"
            return
        }
        if (precioUpdate == null) {
            binding.tvSearch.text = "Por favor ingrese un porcentaje válido"
            return
        }


        documentRef.get()
            .addOnSuccessListener { result ->

                for (document in result) {
                    val precioActual = document.getDouble("Precio")
                    val documentRef = document.reference
                    if (precioActual != null) {
                        val incremento = precioActual * (precioUpdate / 100)
                        val nuevoPrecio = precioActual + incremento
                        documentRef.update("Precio", nuevoPrecio)
                        Log.i(
                            "corchometro",
                            "quiero que muestre ${update.text} que es el precio y ${nuevoPrecio}"
                        )
                    }
                }
            }.addOnSuccessListener {
                Log.d(TAG, "DocumentSnapshot successfully updated!")
                binding.tvSearch.text = "Precios Actualizados"
                binding.etCodigo.text.clear()
            }.addOnFailureListener { e ->
                Log.w(TAG, "Error updating document", e)
                binding.tvSearch.text = "Error al actualizar el precio"
            }
    }


    @SuppressLint("SetTextI18n")
    private fun deleteData() {
        val etCodigo = binding.etCodigo
        if (etCodigo.text.isNullOrEmpty()) {
            binding.tvSearch.text = "Por favor, ingrese un Codigo"
            return
        }
        val docRef = db.collection("cities").document("${etCodigo.text}")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                docRef.delete()
                Log.i("corchometro", "DocumentSnapshot successfully deleted! SI Se borr")
                binding.tvSearch.text = "Se ELIMINO el producto correctamente"
                binding.etCodigo.text.clear()


            }else{
                binding.tvSearch.text = "No se encontro el codigo."
            }
        }
            .addOnFailureListener { exception ->
                Log.i("corchometro", "No p99ude borrar")
                binding.tvSearch.text = "No se pudo eliminar el producto. Verifique los datos"
            }
    }
}

