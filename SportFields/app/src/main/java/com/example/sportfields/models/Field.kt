package com.example.sportfields.models

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint

data class Field(
    @DocumentId val id: String = "",
    val userId: String = "",
    val type: String = "",
    val description: String = "",
    val mainImage: String = "",
    val galleryImages: List<String> = emptyList(),
    val location: GeoPoint = GeoPoint(0.0, 0.0)
)
