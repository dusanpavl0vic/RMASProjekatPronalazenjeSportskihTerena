package com.example.sportfields.repositories

import android.net.Uri
import com.example.sportfields.models.Field
import com.google.android.gms.maps.model.LatLng

interface FieldRepository {
    suspend fun getAllFields(): Resource<List<Field>>
    suspend fun getUserFields(userId: String): Resource<List<Field>>
    suspend fun saveField(
        type: String,
        description: String,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String>
}