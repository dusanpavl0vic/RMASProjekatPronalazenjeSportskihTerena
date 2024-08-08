package com.example.sportfields.repositories

import android.net.Uri
import com.example.sportfields.models.Field
import com.example.sportfields.services.DatabaseService
import com.example.sportfields.services.StorageService
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class FieldRepositoryImp : FieldRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val databaseService =  DatabaseService(firestore)
    private val storageService = StorageService(storage)

    override suspend fun getAllFields(): Resource<List<Field>> {
        return try{
            val snapshot = firestore.collection("Fields").get().await()
            val Fields = snapshot.toObjects(Field::class.java)
            Resource.Success(Fields)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUserFields(userId: String): Resource<List<Field>> {
        return try {
            val snapshot = firestore.collection("Fields")
                .whereEqualTo("userId", userId)
                .get()
                .await()
            val Fields = snapshot.toObjects(Field::class.java)
            Resource.Success(Fields)
        }catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun saveField(
        type: String,
        description: String,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: LatLng
    ): Resource<String> {
        return try{
            val user = firebaseAuth.currentUser
            if(user != null){
                val mainImageUrl = storageService.uploadFieldLogo(mainImage)
                val galleryImageUrls = storageService.uploadFieldImages(galleryImages)
                val geoLocation = GeoPoint(
                    location.latitude,
                    location.longitude
                )
                val Field = Field(
                    userId = user.uid,
                    type = type,
                    description = description,
                    mainImage = mainImageUrl,
                    galleryImages = galleryImageUrls,
                    location = geoLocation
                )
                databaseService.saveField(Field)
                databaseService.addScore(user.uid, 10)
            }
            Resource.Success("Uspesno dodat teren")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}