package com.example.proyecto_iot_nota_3

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import java.util.Date

/**
 * Modelo de datos para una Noticia.
 * Los nombres de las variables deben coincidir con los campos en Firestore.
 *
 * @param id Usaremos el ID del documento de Firestore (no se guarda en el documento en sí).
 * @param title Título de la noticia.
 * @param summary Resumen breve para la lista. (¡AGREGADO!)
 * @param content Contenido completo de la noticia.
 * @param author Autor de la noticia. (¡AGREGADO!)
 * @param date Fecha de creación/publicación.
 */
@IgnoreExtraProperties
data class NewsModel(
    @get:Exclude var id: String = "",
    val title: String = "",
    val summary: String = "",
    val content: String = "",
    val author: String = "",
    val date: Date = Date()
)