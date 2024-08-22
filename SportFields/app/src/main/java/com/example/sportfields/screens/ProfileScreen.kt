package com.example.sportfields.screens

import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoNotDisturb
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sportfields.models.Field
import com.example.sportfields.models.User
import com.example.sportfields.repositories.Resource
import com.example.sportfields.ui.theme.mainColor
import com.example.sportfields.viewmodels.FieldViewModel
import com.google.gson.Gson
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun ProfileScreen(
    navController: NavController,
    fieldViewModel: FieldViewModel,
    user: User
) {
    fieldViewModel.getUserFields(user.id)
    val fieldsResource = fieldViewModel.userFields.collectAsState()
    val fields = remember {
        mutableStateListOf<Field>()
    }

    fieldsResource.value.let {
        when(it){
            is Resource.Success -> {
                fields.clear()
                fields.addAll(it.result)
            }
            is Resource.loading -> {

            }
            is Resource.Failure -> {
                Log.e("Podaci", it.toString())
            }
            null -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .height(300.dp)
                        .background(
                            mainColor,
                            shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        //horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.Start
                        ) {
                            ProfileImage(imageUrl = user.image) // The Profile Image on the left
                        }
                        Spacer(modifier = Modifier.width(10.dp)) // Space between the image and text
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.End
                        ) {
                            Text(
                                text = user.fullName.replace("+", " "),
                                color = Color.White,
                                fontSize = 22.sp
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            Row {
                                Text(
                                    text = fields.count().toString(),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Dodatih terena",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Thin)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row {
                                Text(
                                    text = user.score.toString(),
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Broj poena",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    style = TextStyle(fontWeight = FontWeight.Thin)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)

                            ) {
                                Icon(imageVector = Icons.Filled.Phone, contentDescription = "", tint = Color.White)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(text = user.phone, color = Color.White)
                            }

                        }


                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(16.dp)
                    ) {
                        BackButton {
                            navController.popBackStack()
                        }
                    }
                }
            }
            item { PhotosSection(fields = fields, navController = navController) }
        }
    }
}

@Composable
fun ProfileImage(
    imageUrl: String
){
    Box(modifier = Modifier
        .padding(top = 20.dp)){
        Row {
            AsyncImage(
                model = imageUrl,
                contentDescription = "userimage",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        2.dp,
                        Color.White,
                        shape = RoundedCornerShape(100.dp)
                    )

                    .clip(shape = RoundedCornerShape(100.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun PhotosSection(
    fields: List<Field>,
    navController: NavController
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(text = "Moji tereni")
        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if(fields.isNotEmpty()) {
                for (field in fields) {
                    item {
                        AsyncImage(
                            model = field.mainImage,
                            contentScale = ContentScale.Crop,
                            contentDescription = "",
                            modifier =
                            Modifier
                                .width(100.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(100.dp))
                                .background(
                                    Color.White,
                                    RoundedCornerShape(100.dp)
                                )
                                .clickable {
                                    val fieldJson = Gson().toJson(field)
                                    val encodedFieldJson = URLEncoder.encode(
                                        fieldJson,
                                        StandardCharsets.UTF_8.toString()
                                    )
                                    //TODO: navigacija do screena za field
                                }
                        )
                    }
                }
            }else{
                item {
                    Image(
                        imageVector = Icons.Filled.DoNotDisturb,
                        contentScale = ContentScale.Crop,
                        contentDescription = "",
                        modifier =
                        Modifier
                            .width(150.dp)
                            .height(150.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Color.Gray,
                                RoundedCornerShape(20.dp)
                            )
                    )
                }
            }
        }
    }
}