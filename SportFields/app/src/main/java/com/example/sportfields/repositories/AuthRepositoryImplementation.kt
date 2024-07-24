package com.example.sportfields.repositories

import android.net.Uri
import com.example.sportfields.models.User
import com.example.sportfields.services.DatabaseService
import com.example.sportfields.services.StorageService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class AuthRepositoryImplementation : AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val databaseService =  DatabaseService(firestore)
    private val storageService = StorageService(storage)

    override val user: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): Resource<FirebaseUser> {
        return try{
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Resource.Success(result.user!!)
        }catch (e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun register(
        image: Uri,
        fullName: String,
        email: String,
        phone: String,
        password: String
    ): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()

            if(result.user != null){
                val imageUrl = storageService.uploadUserImage(result.user!!.uid, image)
                val user = User(
                    fullName = fullName,
                    phone = phone,
                    image = imageUrl
                )
                databaseService.registerUser(result.user!!.uid, user)
            }
            Resource.Success(result.user!!)
        }catch(e: Exception){
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getUser(): Resource<User> {
        return try {
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val uid = currentUser.uid

                val db = FirebaseFirestore.getInstance()

                val userDocRef = db.collection("users").document(uid)
                val userSnapshot = userDocRef.get().await()

                if (userSnapshot.exists()) {
                    val customUser = userSnapshot.toObject(User::class.java)
                    if (customUser != null) {
                        Resource.Success(customUser)
                    } else {
                        Resource.Failure(Exception("Neuspešno mapiranje dokumenta na CustomUser"))
                    }
                } else {
                    Resource.Failure(Exception("Korisnikov dokument ne postoji"))
                }
            } else {
                Resource.Failure(Exception("Nema trenutnog korisnika"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }

    override suspend fun getAllUsers(): Resource<List<User>> {
        return try {
            val db = FirebaseFirestore.getInstance()
            val usersCollectionRef = db.collection("users")
            val usersSnapshot = usersCollectionRef.get().await()

            if (!usersSnapshot.isEmpty) {
                val usersList = usersSnapshot.documents.mapNotNull { document ->
                    document.toObject(User::class.java)
                }
                Resource.Success(usersList)
            } else {
                Resource.Failure(Exception("Nema korisnika u bazi podataka"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Failure(e)
        }
    }
}