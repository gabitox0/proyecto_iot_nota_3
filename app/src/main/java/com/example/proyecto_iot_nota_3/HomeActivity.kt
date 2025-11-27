package com.example.proyecto_iot_nota_3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
// Asumiendo que has renombrado activity_main.xml a activity_home.xml o usas ActivityMainBinding
import com.example.proyecto_iot_nota_3.databinding.ActivityMainBinding // O ActivityHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

/**
 * Activity Home: Muestra la lista de noticias de Firestore.
 * Maneja la navegación al detalle, el formulario de agregar noticia y el cierre de sesión.
 */
class HomeActivity : AppCompatActivity() {

    // Nota: Si el archivo XML es activity_main.xml, usa ActivityMainBinding.
    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var newsAdapter: NewsAdapter

    // Colección de noticias en Firestore
    private val NEWS_COLLECTION = "news"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Nota: Si el layout es activity_home.xml, cambia ActivityMainBinding.inflate por ActivityHomeBinding.inflate
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        setupUserInterface() // Botón Salir y FAB (+)
        setupRecyclerView()  // RecyclerView y Listener de Clic
        loadNewsFromFirestore() // Carga de datos
    }

    /**
     * Configura el nombre del usuario, el botón de Cerrar Sesión y el FAB (+).
     */
    private fun setupUserInterface() {
        val user = auth.currentUser

        if (user != null) {
            val userName = user.displayName ?: user.email ?: "Usuario"
            binding.tvWelcome.text = "¡Bienvenido, $userName!" // tvWelcome debe existir en el layout
        }

        // Listener 1: Botón de Cerrar Sesión (REQUISITO: Botón Cerrar Sesión)
        binding.btnLogout.setOnClickListener { // btnLogout debe existir en el layout
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Listener 2: Floating Action Button (FAB) para Agregar Noticia (REQUISITO: Agregar noticia)
        // fabAddNews debe existir en el layout, como se agregó anteriormente.
        binding.fabAddNews.setOnClickListener {
            val intent = Intent(this, AddNewsActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Inicializa el RecyclerView, el Adapter y el Listener de clic en las tarjetas.
     */
    private fun setupRecyclerView() {
        // Inicializa el adaptador con una lista vacía y el listener de clic (REQUISITO: Ver Noticia)
        newsAdapter = NewsAdapter(emptyList()) { selectedNews ->
            // Navega a DetailActivity, pasando el ID del documento
            val intent = Intent(this, DetailActivity::class.java).apply {
                putExtra("NEWS_ID", selectedNews.id)
            }
            startActivity(intent)
        }

        // recyclerViewNews debe existir en el layout
        binding.recyclerViewNews.apply {
            layoutManager = LinearLayoutManager(this@HomeActivity)
            adapter = newsAdapter
        }
    }

    /**
     * Escucha los cambios en la colección 'news' de Firestore en tiempo real (REQUISITO: Home que lee noticias).
     */
    private fun loadNewsFromFirestore() {
        db.collection(NEWS_COLLECTION)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("HomeActivity", "Error al escuchar noticias:", e)
                    Toast.makeText(this, "Error al cargar las noticias.", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val newsList = mutableListOf<NewsModel>()
                    for (document in snapshot.documents) {
                        val news = document.toObject(NewsModel::class.java)
                        news?.let {
                            // Asigna el ID del documento, clave para el Detalle
                            it.id = document.id
                            newsList.add(it)
                        }
                    }
                    // Actualiza la lista dinámicamente
                    newsAdapter.updateList(newsList)
                } else {
                    Log.d("HomeActivity", "No hay noticias o la colección está vacía.")
                    newsAdapter.updateList(emptyList())
                }
            }
    }
}