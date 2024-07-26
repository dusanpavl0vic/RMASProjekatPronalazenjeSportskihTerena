package com.example.sportfields.services


import com.example.sportfields.models.User
import com.example.sportfields.repositories.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DatabaseService(
    private val firestore: FirebaseFirestore
) {
    suspend fun registerUser(
        userId: String,
        user: User
    ): Resource<String>{
        return try{
            firestore.collection("users").document(userId).set(user).await()
            Resource.Success("Uspešno registrovan korisnik")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

}

