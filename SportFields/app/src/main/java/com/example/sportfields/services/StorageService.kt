package com.example.sportfields.services

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class  StorageService(
    private val storage: FirebaseStorage
) {
    suspend fun uploadUserImage(userId: String, image: Uri): String{
        return try{
            val storageRef = storage.reference.child("user_images/$userId.jpg")
            val uploadTask = storageRef.putFile(image).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }catch (e: Exception){
            e.printStackTrace()
            ""
        }
    }
    suspend fun uploadPlaceLogo(
        image: Uri
    ): String{
        return try{
            val fileName = "${System.currentTimeMillis()}.jpg"
            val storageRef = storage.reference.child("places_images/logos/$fileName")
            val uploadTask = storageRef.putFile(image).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()
            downloadUrl.toString()
        }catch (e: Exception){
            e.printStackTrace()
            ""
        }
    }
    suspend fun uploadPlaceImages(
        images: List<Uri>
    ): List<String>{
        val downloadUrls = mutableListOf<String>()
        for (image in images) {
            try {
                val fileName = "${System.currentTimeMillis()}.jpg"
                val storageRef = storage.reference.child("places_images/images/$fileName")
                val uploadTask = storageRef.putFile(image).await()
                val downloadUrl = uploadTask.storage.downloadUrl.await()
                downloadUrls.add(downloadUrl.toString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return downloadUrls
    }
}