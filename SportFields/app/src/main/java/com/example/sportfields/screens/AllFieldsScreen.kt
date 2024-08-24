package com.example.sportfields.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.ContentPasteSearch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sportfields.models.Field
import com.example.sportfields.navigation.Routes
import com.example.sportfields.repositories.Resource
import com.example.sportfields.ui.theme.mainColor
import com.example.sportfields.viewmodels.FieldViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AllFieldsScreen(
    fields: List<Field>?,
    navController: NavController,
    fieldViewModel: FieldViewModel
) {
    val fieldsList = remember {
        mutableListOf<Field>()
    }
    if(fields.isNullOrEmpty()){
        val fieldsResource = fieldViewModel.fields.collectAsState()
        fieldsResource.value.let{
            when(it){
                is Resource.Success -> {
                    fieldsList.clear()
                    fieldsList.addAll(it.result)
                }
                is Resource.loading -> {

                }
                is Resource.Failure -> {
                }
                null -> {}
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(10.dp)
        ) {
            BackButton {
                navController.popBackStack()
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Image(imageVector = Icons.Filled.ContentPasteSearch, contentDescription = "", modifier = Modifier.size(100.dp))
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Svi tereni",
                        modifier = Modifier.fillMaxWidth(),
                        style= TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if(fields.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            imageVector = Icons.Filled.CloudOff,
                            contentDescription = "",
                            modifier = Modifier.size(150.dp)
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = "Trenutno nema terena")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        fields.forEach{ field ->
                            FieldRow(
                                field = field,
                                fieldScreen = {
                                    val fieldJson = Gson().toJson(field)
                                    val encoded = URLEncoder.encode(fieldJson, StandardCharsets.UTF_8.toString())
                                    navController.navigate(Routes.fieldScreen + "/$encoded")
                                }
                            )
                            Spacer(modifier = Modifier.height(5.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FieldRow(
    field: Field,
    fieldScreen: () -> Unit,
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { fieldScreen() }
            .border(2.dp, mainColor ,RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Image section with new design and border
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(8.dp))
                .border(2.dp, Color.Gray, RoundedCornerShape(8.dp))
        ) {
            AsyncImage(
                model = field.mainImage,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (field.description.length > 20) {
                    "${field.description.substring(0, 20).replace('+', ' ')}..."
                } else {
                    field.description.replace('+', ' ')
                },
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Jos podataka...",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            )
        }
    }
}