package com.example.proyecto_iot_nota_3

import android.os.Bundle
import android.util.Log // Asegúrate de tener esta importación
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecto_iot_nota_3.databinding.ActivityDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var db: FirebaseFirestore
    // Formato de fecha para mostrar correctamente
    private val dateFormat = SimpleDateFormat("dd MMMM yyyy (HH:mm)", Locale("es", "ES"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        // Obtener el ID de la noticia enviado desde HomeActivity
        val newsId = intent.getStringExtra("NEWS_ID")

        if (newsId != null) {
            // Opcional: Mostrar ProgressBar al iniciar la carga
            binding.progressBar.visibility = View.VISIBLE
            binding.contentLayout.visibility = View.GONE
            loadNewsDetail(newsId)
        } else {
            Toast.makeText(this, "Error: No se encontró el ID de la noticia.", Toast.LENGTH_LONG).show()
            finish()
        }

        // Botón Volver a Noticias
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadNewsDetail(id: String) {
        db.collection("news").document(id)
            .get()
            .addOnSuccessListener { document ->
                // Ocultar ProgressBar independientemente del resultado
                binding.progressBar.visibility = View.GONE

                if (document.exists()) {
                    val news = document.toObject(NewsModel::class.java)
                    if (news != null) {
                        displayNews(news)
                        binding.contentLayout.visibility = View.VISIBLE
                    } else {
                        // CORRECCIÓN DE ERROR 1: Toast.SHORT -> Toast.LENGTH_SHORT
                        Toast.makeText(this, "Error al mapear la noticia.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                } else {
                    // CORRECCIÓN DE ERROR 2: Toast.SHORT -> Toast.LENGTH_SHORT
                    Toast.makeText(this, "Noticia no encontrada.", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener { e ->
                // Ocultar ProgressBar en caso de fallo
                binding.progressBar.visibility = View.GONE

                Log.e("DetailActivity", "Error al cargar el detalle: ", e)
                // CORRECCIÓN DE ERROR 3: Toast.SHORT -> Toast.LENGTH_SHORT
                Toast.makeText(this, "Fallo en la conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                finish()
            }
    }

    private fun displayNews(news: NewsModel) {
        binding.tvDetailTitle.text = news.title
        binding.tvDetailSummary.text = news.summary
        binding.tvDetailContent.text = news.content
        binding.tvDetailAuthor.text = "Autor: ${news.author}"
        binding.tvDetailDate.text = "Publicado: ${dateFormat.format(news.date)}"

        // La visibilidad se maneja en loadNewsDetail
    }
}