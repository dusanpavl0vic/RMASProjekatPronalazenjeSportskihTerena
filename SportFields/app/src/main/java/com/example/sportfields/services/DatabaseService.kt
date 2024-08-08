package com.example.sportfields.services


import com.example.sportfields.models.Field
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

    suspend fun getUser(
        uid: String
    ):Resource<String>{
        return try {
            val userReference = firestore.collection("users").document(uid)
            val userSnapshot = userReference.get().await()

            if(userSnapshot.exists()){
                val user = userSnapshot.toObject(User::class.java)
                if(user != null){
                    Resource.Success(user)
                } else {
                    Resource.Failure(Exception("Korisnik ne postoji"))
                }
            } else {
                Resource.Failure(Exception("Korisnikov dokument ne postoji"))
            }
            Resource.Success("Bravoo")
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun saveField(field: Field): Resource<String>{
        return try{
            firestore.collection("places").add(field).await()
            Resource.Success("Uspešno dodato mesto")
        }catch(e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    suspend fun addScore(
        userId: String,
        value: Int
    ): Resource<String>{
        return try{
            val userReference = firestore.collection("users").document(userId)
            val userSnapshot = userReference.get().await()
            if(userSnapshot.exists()){
                val user = userSnapshot.toObject(User::class.java)
                if(user != null){
                    val newScore = user.score + value;
                    userReference.update("score", newScore).await()
                    Resource.Success("Povecan je score korisnika")
                }else{
                    Resource.Failure(Exception("Korisnik ne postoji"))
                }
            }
            else{
                Resource.Failure(Exception("Korisnikov dokument ne postoji"))
            }
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

}

