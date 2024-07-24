package com.example.sportfields.repositories

import android.net.Uri
import com.example.sportfields.models.User
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    val user : FirebaseUser?

    suspend fun login(email: String, password: String): Resource<FirebaseUser>
    fun logout()
    suspend fun register (image: Uri, fullName: String, email: String, phone: String, password: String): Resource<FirebaseUser>
    suspend fun getUser(): Resource<User>
    suspend fun getAllUsers(): Resource<List<User>>

}