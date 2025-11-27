package com.example.proyecto_iot_nota_3

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecto_iot_nota_3.databinding.ItemNewsBinding
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Adapter para el RecyclerView que muestra la lista de objetos NewsModel.
 *
 * Se ha modificado para incluir el listener de clic (onItemClick).
 */
class NewsAdapter(
    private var newsList: List<NewsModel>,
    // 1. AÑADIMOS ESTE ARGUMENTO: La función lambda para manejar el clic
    private val onItemClick: (NewsModel) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    inner class NewsViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(news: NewsModel, onItemClick: (NewsModel) -> Unit) {
            // Asumiendo que NewsModel tiene 'summary' (si no, usa 'content')
            binding.tvNewsTitle.text = news.title
            binding.tvNewsContent.text = news.summary

            binding.tvNewsDate.text = "Fecha: ${dateFormat.format(news.date)}"

            // 2. Añadimos el OnClickListener a la vista raíz del item
            binding.root.setOnClickListener {
                onItemClick(news) // Ejecuta el callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        // Pasamos el objeto NewsModel y la función de clic al bind
        holder.bind(newsList[position], onItemClick)
    }

    override fun getItemCount(): Int = newsList.size

    fun updateList(newList: List<NewsModel>) {
        newsList = newList
        notifyDataSetChanged()
    }
}