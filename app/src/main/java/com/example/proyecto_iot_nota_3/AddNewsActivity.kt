package com.example.proyecto_iot_nota_3

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_iot_nota_3.databinding.ActivityAddNewsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class AddNewsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddNewsBinding
    // Variables lateinit de Firebase
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()

        setupListeners()
    }

    /**
     * Define y configura los listeners para el botón de guardar y el botón de volver.
     */
    private fun setupListeners() {
        // btnCreateNews debe existir en el XML
        binding.btnCreateNews.setOnClickListener {
            saveNewsToFirestore()
        }

        // btnBack debe existir en el XML
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    /**
     * Lógica para guardar la nueva noticia en Firebase Firestore.
     */
    private fun saveNewsToFirestore() {
        // Obtiene valores de los EditText (IDs del XML)
        val title = binding.etNewsTitle.text.toString().trim()
        val summary = binding.etNewsSummary.text.toString().trim()
        val content = binding.etNewsContent.text.toString().trim()

        // Asigna el autor y la fecha
        val author = auth.currentUser?.email ?: "Autor Desconocido"
        val date = Date()

        // Validación según rúbrica
        if (title.isEmpty() || summary.isEmpty() || content.isEmpty()) {
            showAlertDialog("Error de Validación", "Por favor, completa los campos de Título, Resumen y Contenido.")
            return
        }

        // Creamos el mapa de datos (debe coincidir con NewsModel)
        val newsData = hashMapOf(
            "title" to title,
            "summary" to summary,
            "content" to content,
            "author" to author,
            "date" to date
        )

        // Guardar en Firestore (REQUISITO: Escribir en Firestore)
        db.collection("news")
            .add(newsData)
            .addOnSuccessListener {
                showAlertDialog("Éxito", "Noticia guardada correctamente en Firestore.")
                clearFields()
            }
            .addOnFailureListener { e ->
                showAlertDialog("Error de Guardado", "Fallo al guardar la noticia: ${e.message}")
                Log.e("AddNewsActivity", "Error al guardar: ", e)
            }
    }

    private fun clearFields() {
        binding.etNewsTitle.setText("")
        binding.etNewsSummary.setText("")
        binding.etNewsContent.setText("")
    }

    /**
     * Muestra un AlertDialog (REQUISITO: AlertDialog para confirmación y error).
     */
    private fun showAlertDialog(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}