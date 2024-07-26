package com.example.sportfields.viewmodels

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportfields.models.User
import com.example.sportfields.repositories.AuthRepositoryImplementation
import com.example.sportfields.repositories.Resource
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _currentUserFlow = MutableStateFlow<Resource<User>?>(null)
    val currentUserFlow: StateFlow<Resource<User>?> = _currentUserFlow

    val repository = AuthRepositoryImplementation()
    private val _loginFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val loginFlow: StateFlow<Resource<FirebaseUser>?> = _loginFlow

    private val _registerFlow = MutableStateFlow<Resource<FirebaseUser>?>(null)
    val registerFlow: StateFlow<Resource<FirebaseUser>?> = _registerFlow

    val currentUser: FirebaseUser?
        get() = repository.user

    init {
        if(repository.user != null){
            _loginFlow.value = Resource.Success(repository.user!!)
        }
    }

    fun login(email: String, password: String) = viewModelScope.launch{
        _loginFlow.value = Resource.loading
        val result = repository.login(email, password)
        _loginFlow.value = result
    }

    fun register(profileImage: Uri, fullName: String, email: String, phoneNumber: String, password: String) = viewModelScope.launch{
        _registerFlow.value = Resource.loading
        val result = repository.register(profileImage, fullName, email, phoneNumber, password)
        _registerFlow.value = result
    }

    fun logout(){
        repository.logout()
        _loginFlow.value = null
        _registerFlow.value = null
        _currentUserFlow.value = null
    }

    fun getUserData() = viewModelScope.launch {
        val result = repository.getUser()
        _currentUserFlow.value = result
    }

    private val _allUsers = MutableStateFlow<Resource<List<User>>?>(null)
    val allUsers: StateFlow<Resource<List<User>>?> = _allUsers

    fun getAllUserData() = viewModelScope.launch {
        val result = repository.getAllUsers()
        _allUsers.value = result
    }
}