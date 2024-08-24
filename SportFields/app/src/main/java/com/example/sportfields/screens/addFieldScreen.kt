package com.example.sportfields.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sportfields.navigation.Routes
import com.example.sportfields.repositories.Resource
import com.example.sportfields.viewmodels.FieldViewModel
import com.google.android.gms.maps.model.LatLng

@Composable
fun addFieldScreen(
    fieldViewModel: FieldViewModel?,
    location: MutableState<LatLng>?,
    navController: NavController
) {
    val fieldFlow = fieldViewModel?.fieldFlow?.collectAsState()
    val selectedImage = remember {
        mutableStateOf<Uri?>(Uri.EMPTY)
    }
    var selectedType = remember { mutableStateOf(FieldType.KOSARKA) }

    val description = remember {
        mutableStateOf("")
    }
    val selectedImages = remember {
        mutableStateOf<List<Uri>>(emptyList())
    }
    val buttonIsEnabled = remember {
        mutableStateOf(true)
    }
    val buttonIsLoading = remember {
        mutableStateOf(false)
    }

    val isAdded = remember {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 50.dp, horizontal = 20.dp)
    ) {
        item{
            BackButton {
                navController.popBackStack()
            }
        }
        item{ Heading1Text(textValue = "Dodaj novi teren")}
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item {
            LabelForInput(textValue = "Tip")
        }
        item {
            FieldTypeDropdown(selectedType = selectedType.value, onTypeSelected = { selectedType.value = it })
        }
        item{ ImageLogo(selectedImageUri = selectedImage)}
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item{ LabelForInput(textValue = "Opis")}
        item{ TextArea(inputValue = description, inputText = "Dodaj opis za teren")}
        item{ Spacer(modifier = Modifier.height(20.dp)) }
        item{ LabelForInput(textValue = "Dodaj slike terena")}
        item{ GalleryForPlace(selectedImages = selectedImages)}
        item{ Spacer(modifier = Modifier.height(10.dp)) }
        item{
            LoginRegisterButton(icon = Icons.Filled.Add ,buttonText = "Dodaj mesto", isEnabled = buttonIsEnabled, isLoading = buttonIsLoading) {
                isAdded.value = true
                buttonIsLoading.value = true
                fieldViewModel?.saveField(
                    type = selectedType.value.toString(),
                    description = description.value,
                    mainImage = selectedImage.value!!,
                    galleryImages = selectedImages.value,
                    location = location
                )
            }
        }
    }

    fieldFlow?.value.let {
        when(it){
            is Resource.Failure -> {
                Log.d("Stanje flowa", it.toString());
                buttonIsLoading.value = false
                val context = LocalContext.current

                Toast.makeText(context, "Greska pri dodavanju", Toast.LENGTH_LONG).show()
            }
            is Resource.loading -> {

            }
            is Resource.Success -> {
                Log.d("Stanje flowa", it.toString())
                buttonIsLoading.value = false
                if(isAdded.value)
                    navController.navigate(Routes.firstScreen)
                fieldViewModel?.getAllFields()
            }
            null -> {}
        }
    }
}