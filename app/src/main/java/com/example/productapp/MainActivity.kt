package com.example.productapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
        binding.btTodos.setOnClickListener(View.OnClickListener {
            mostrarTodos()

        })
    }

    private fun mostrarTodos() {
        val docRef = db.collection("productos")
        docRef.get().addOnSuccessListener { result ->
            val stringBuilder = StringBuilder()
            for (document in result) {
                stringBuilder.append("ID: ${document.id}\n")
                for ((key, value) in document.data) {
                    stringBuilder.append("\t$key: $value\n")
                }
                stringBuilder.append("\n") // Añadir una línea en blanco entre documentos

            }
            val allDocument = stringBuilder.toString()

            val intent = Intent(this, AllDocumentActivity::class.java)
            intent.putExtra("Extra", allDocument)
            startActivity(intent)

        }.addOnFailureListener { exception ->
            Log.d(TAG, "Error getting documents: ", exception)
            // Mostrar el mensaje de error en el TextView
            Toast.makeText(applicationContext, "Error al obtener documentos", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addData() {
        val etCodigo = binding.etCodigo
        val etPrecio = binding.etPrecio
       // etPrecio.inputType = InputType.TYPE_CLASS_NUMBER
        val etProducto = binding.etProducto
        val etDescription = binding.etDescription


        if (etCodigo.text.isNullOrEmpty() || etPrecio.text.isNullOrEmpty() || etDescription.text.isNullOrEmpty() || etProducto.text.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "Complete todos los datos", Toast.LENGTH_SHORT).show()
            return
        }

        val Productos = hashMapOf(
            "Codigo" to etCodigo.text.toString(),
            "Producto" to etProducto.text.toString(),
            "Precio" to etPrecio.text.toString().toDouble(),
            "Descripcion" to etDescription.text.toString()

        )
        val docRef = db.collection("productos").document("${etCodigo.text}")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                //binding.tvSearch.text =
                Toast.makeText(
                    applicationContext,
                    "Codigo ya registrado. Pruebe con otro",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                Log.i("corcho", "Agrega un algo. se apreto el boton")
                docRef.set(Productos)
                    .addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully written!")
                        binding.etDescription.text.clear()
                        binding.etCodigo.text.clear()
                        binding.etPrecio.text.clear()
                        binding.etProducto.text.clear()
                        Toast.makeText(applicationContext, "Se agrego el producto correctamente", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error writing document", e)
                        Toast.makeText(applicationContext, "No se pudo agregar el producto. Verifique los datos", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun getData() {
        val etCodigo = binding.etCodigo.text
        if (etCodigo.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "Por favor, ingrese un Codigo", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val docRef = db.collection("productos").document("${etCodigo}")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {
                val data = document.data
                val formattedData = StringBuilder()
                for ((key, value) in data!!) {
                    formattedData.append("$key: $value\n")
                }
                Log.i("corchometro", "DocumentSnapshot data: ${document.data}")

                showData(formattedData.toString(), etCodigo.toString())
            } else {
                Toast.makeText(
                    applicationContext,
                    "Codigo NO encontrado. Pruebe con otro",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }.addOnFailureListener { exception ->
            Log.w(TAG, "Error deleting document", exception)
            Toast.makeText(applicationContext, "No se encuentra el codigo", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun showData(formattedData: String, etCodigo: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_add)
        val tvResolt: TextView = dialog.findViewById(R.id.tvResolt)
        val tvResoltTitle: TextView = dialog.findViewById(R.id.tvResoltTitle)
        val btnBack: Button = dialog.findViewById(R.id.btnBack)
        tvResolt.text = formattedData
        tvResoltTitle.text = etCodigo

        dialog.show()
        btnBack.setOnClickListener { dialog.hide() }

    }


    private fun updateData() {
        val update = binding.etPrecio
        val precioUpdate = update.text.toString().toDoubleOrNull()
        val documentRef = db.collection("productos")

        Log.i("corcho", "ACTUALIZAR se apreto el boton")

        //control de errores

        if (update.text.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "Ingrese un porcentaje valido", Toast.LENGTH_SHORT)
                .show()
            return
        }
        if (precioUpdate == null) {
            Toast.makeText(applicationContext, "Ingrese un porcentaje valido", Toast.LENGTH_SHORT)
                .show()
            return
        }
        showUpdate() { eleccion ->
            if (eleccion) {
                documentRef.get()
                    .addOnSuccessListener { result ->

                        for (document in result) {
                            val precioActual = document.getDouble("Precio")
                            val documentRef = document.reference
                            if (precioActual != null) {

                                val incremento = precioActual * (precioUpdate / 100)
                                val nuevoPrecio = precioActual + incremento
                                documentRef.update("Precio", nuevoPrecio)
                            }
                        }
                    }.addOnSuccessListener {
                        Log.d(TAG, "DocumentSnapshot successfully updated!")
                        Toast.makeText(
                            applicationContext,
                            "PRECIOS ACTUALIZADOS",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.etPrecio.text.clear()
                    }.addOnFailureListener { e ->
                        Log.w(TAG, "Error updating document", e)
                        Toast.makeText(
                            applicationContext,
                            "Error al actualizar el precio",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            } else {
                Toast.makeText(
                    applicationContext,
                    "NO se actualizaron los precios",
                    Toast.LENGTH_SHORT
                ).show()


            }
        }
    }

    private fun showUpdate(callback: (Boolean) -> Unit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_update)
        val btnUpdate: Button = dialog.findViewById(R.id.btnUpdate)
        val btnBack: Button = dialog.findViewById(R.id.btnBack)


        dialog.show()
        btnUpdate.setOnClickListener {
            callback(true)
            dialog.hide()
        }


        btnBack.setOnClickListener {
            callback(false)
            dialog.hide()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun deleteData() {
        val etCodigo = binding.etCodigo.text
        if (etCodigo.isNullOrEmpty()) {
            Toast.makeText(applicationContext, "Ingrese un codigo", Toast.LENGTH_SHORT).show()
            return
        }
        val docRef = db.collection("productos").document("${etCodigo}")
        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.data != null) {

                Log.i("corchometro", "DocumentSnapshot successfully deleted! SI Se borr")

                showDelete(etCodigo.toString()) { eleccion ->
                    if (eleccion) {
                        docRef.delete()
                        Toast.makeText(
                            applicationContext,
                            "se ELIMINO el producto correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        binding.etCodigo.text.clear()

                    } else {
                        Toast.makeText(
                            applicationContext,
                            "No se elimino el producto",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }


            } else {
                Toast.makeText(applicationContext, "No se encontro el producto", Toast.LENGTH_SHORT)
                    .show()
            }
        }
            .addOnFailureListener { exception ->
                Log.i("corchometro", "No pude borrar")
                Toast.makeText(
                    applicationContext,
                    "No se pudo eliminar el producto. Verifique los datos",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun showDelete(etCodigo: String, callback: (Boolean) -> Unit) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_sure)


        val btnYes: Button = dialog.findViewById(R.id.btnYes)
        val btnNo: Button = dialog.findViewById(R.id.btnNo)
        val tvSureText: TextView = dialog.findViewById(R.id.tvSureText)

        //var eleccion = false


        tvSureText.text = "Esta por eliminar $etCodigo ¿Esta seguro?"

        dialog.show()
        btnYes.setOnClickListener {
            callback(true)
            dialog.hide()
        }


        btnNo.setOnClickListener {
            callback(false)
            dialog.hide()

        }
    }
}



