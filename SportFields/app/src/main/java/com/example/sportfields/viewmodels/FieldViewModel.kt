package com.example.sportfields.viewmodels

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportfields.models.Field
import com.example.sportfields.repositories.FieldRepositoryImp
import com.example.sportfields.repositories.Resource
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FieldViewModel : ViewModel(){
    val repository = FieldRepositoryImp()

    private val _fieldFlow = MutableStateFlow<Resource<String>?>(null)
    val FieldFlow: StateFlow<Resource<String>?> = _fieldFlow

    private val _newScore = MutableStateFlow<Resource<String>?>(null)
    val newScore: StateFlow<Resource<String>?> = _newScore

    private val _fields = MutableStateFlow<Resource<List<Field>>>(Resource.Success(emptyList()))
    val Fields: StateFlow<Resource<List<Field>>> get() = _fields

    private val _userFields = MutableStateFlow<Resource<List<Field>>>(Resource.Success(emptyList()))
    val userFields: StateFlow<Resource<List<Field>>> get() = _userFields

    private val _newMark = MutableStateFlow<Resource<String>?>(null)
    val newMark: StateFlow<Resource<String>?> = _newMark

    fun getAllFields() = viewModelScope.launch {
        _fields.value = repository.getAllFields()
    }

    fun getUserFields(userId: String) = viewModelScope.launch {
        _userFields.value = repository.getUserFields(userId)
    }

    init{
        getAllFields()
    }

    fun saveField(
        type: String,
        description: String,
        mainImage: Uri,
        galleryImages: List<Uri>,
        location: MutableState<LatLng>?
    ) = viewModelScope.launch {
        _fieldFlow.value = Resource.loading
        repository.saveField(
            type = type,
            description = description,
            mainImage = mainImage,
            galleryImages = galleryImages,
            location = location!!.value
        )
        _fieldFlow.value = Resource.Success("Uspesno dodat teren")
    }
}