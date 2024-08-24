package com.example.sportfields.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sportfields.R
import com.example.sportfields.models.Field
import com.example.sportfields.models.Score
import com.example.sportfields.navigation.Routes
import com.example.sportfields.repositories.Resource
import com.example.sportfields.ui.theme.greyTextColor
import com.example.sportfields.ui.theme.mainColor
import com.example.sportfields.viewmodels.FieldViewModel
import com.example.sportfields.viewmodels.LoginViewModel
import com.google.gson.Gson
import java.math.RoundingMode
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@Composable
fun FieldScreen(
    navController: NavController,
    fieldViewModel: FieldViewModel,
    loginViewModel: LoginViewModel,
    field: Field,
    fields: MutableList<Field>?
) {
    val scores = remember { mutableStateListOf<Score>() }
    val averageScore = remember { mutableStateOf(0.0) }
    val myScore = remember { mutableStateOf("") }
    val showScoreScreen = remember { mutableStateOf(false) }
    val isLoading = remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(mainColor)) {

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            item {
                BackButton {
                    if (fields == null) {
                        navController.popBackStack()
                    } else {
                        val isCameraSet = true
                        val latitude = field.location.latitude
                        val longitude = field.location.longitude

                        val fieldsJson = Gson().toJson(fields)
                        val encodedFieldsJson = URLEncoder.encode(fieldsJson, StandardCharsets.UTF_8.toString())
                        navController.navigate(Routes.firstScreen + "/$isCameraSet/$latitude/$longitude/$encodedFieldsJson")
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    FieldMainImage(imageUrl = field.mainImage)
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            FieldMark(type = field.type)
                            Spacer(modifier = Modifier.width(8.dp))
                            Heading1Text(textValue = field.type.replace('+', ' '))
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(text = field.description.replace('+', ' '))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Prosečna ocena: ${averageScore.value}/10",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        )
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
            item { FieldImagesView(images = field.galleryImages) }
            item { Spacer(modifier = Modifier.height(60.dp)) }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 15.dp, vertical = 20.dp)
        ) {
            ScoreButton(
                onClick = {
                    val scoreExist = scores.firstOrNull {
                        it.fieldId == field.id && it.userId == loginViewModel.currentUser?.uid
                    }
                    if (scoreExist != null) {
                        myScore.value = scoreExist.score.toString()
                    }
                    showScoreScreen.value = true
                },
                enabled = field.userId != loginViewModel.currentUser?.uid,
                type = field.type
            )
        }

        if (showScoreScreen.value) {
            AddScoreScreen(
                showScoreScreen = showScoreScreen,
                isLoading = isLoading,
                score = myScore,
                onClick = {
                    val scoreExist = scores.firstOrNull {
                        it.fieldId == field.id && it.userId == loginViewModel.currentUser?.uid
                    }
                    if (scoreExist != null) {
                        isLoading.value = true
                        fieldViewModel.updateScore(scoreExist.id, myScore.value.toInt())
                    } else {
                        isLoading.value = true
                        fieldViewModel.addScore(
                            field.id,
                            myScore.value.toInt(),
                            field
                        )
                    }
                    showScoreScreen.value = false


                }
            )
        }
    }

    val scoresResources = fieldViewModel.scores.collectAsState()
    val newScoreResource = fieldViewModel.newScore.collectAsState()

    scoresResources.value.let {
        when(it){
            is Resource.Success -> {
                scores.clear()
                scores.addAll(it.result)

                var sum = 0.0
                for (s in it.result){
                    sum += s.score.toDouble()
                }
                Log.e("Skorovi" , sum.toString())
                if (it.result.isNotEmpty()) {
                    val rawPositive = sum / it.result.count()
                    val rounded = rawPositive.toBigDecimal().setScale(1, RoundingMode.UP).toDouble()
                    averageScore.value = rounded
                } else {
                    averageScore.value = 0.0
                }
            }
            is Resource.loading -> {
                // Logika za učitavanje
            }
            is Resource.Failure -> {
                // Logika za grešku
            }
        }
    }
    newScoreResource.value.let {
        when(it){
            is Resource.Success -> {
                isLoading.value = false

                val scoreExist = scores.firstOrNull { score ->
                    score.id == it.result
                }
                if(scoreExist != null && myScore.value != "") {
                    scoreExist.score = myScore.value.toInt()
                }
            }
            is Resource.loading -> {
                // Logika za učitavanje
            }
            is Resource.Failure -> {
                val context = LocalContext.current
                Toast.makeText(context, "Došlo je do greške prilikom ocenjivanja terena", Toast.LENGTH_LONG).show()
                isLoading.value = false
            }
            null -> {
                isLoading.value = false
            }
        }
    }
}

@Composable
fun FieldMark(
    type: String
) {
    val icon = fieldTypeIcons[type] ?: R.drawable.sports
    Box(
        modifier = Modifier
            .size(30.dp) // Increased size for better visibility
            .background(Color.White, shape = CircleShape)
            .border(1.dp, Color.Gray, shape = CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(20.dp) // Small icon inside the circle
        )
    }
}

@Composable
fun FieldMainImage(
    imageUrl: String
) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .border(1.dp, Color.White),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(200.dp)
        )
    }
}



@Composable
fun FieldImagesView(
    images: List<String>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        for (index in images.indices step 2) {
            Row(modifier = Modifier.fillMaxWidth()) {
                AsyncImage(
                    model = images[index],
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(100.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                if (index + 1 < images.size) {
                    AsyncImage(
                        model = images[index + 1],
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(170.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}

@Composable
fun ScoreButton(
    onClick: () -> Unit,
    enabled: Boolean,
    type: String
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = mainColor,
            contentColor = Color.White,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        border = BorderStroke(2.dp, Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(mainColor, RoundedCornerShape(30.dp)),
    ) {
        Text(
            "Oceni  teren za ${type.replace("+", " ")}",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
@Composable
fun AddScoreScreen(
    showScoreScreen: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    score: MutableState<String>,  // Tip `score` vraćen na `String`
    onClick: () -> Unit
) {
    val interactionSource = remember {
        MutableInteractionSource()
    }

    AlertDialog(
        onDismissRequest = { /* TODO */ },
        confirmButton = { /* TODO */ },
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp)
                    .background(Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ocenite kakav je teren",
                            style = TextStyle(
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(40.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(
                                onClick = {
                                    val currentScore = score.value.toIntOrNull() ?: 1
                                    if (currentScore > 1) {
                                        score.value = (currentScore - 1).toString()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector  = Icons.Filled.ArrowDropDown,
                                    contentDescription = "Smanji ocenu",
                                    tint = Color.Black
                                )
                            }
                            Text(
                                text = "${score.value} / 10",
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            IconButton(
                                onClick = {
                                    val currentScore = score.value.toIntOrNull() ?: 1
                                    if (currentScore < 10) {
                                        score.value = (currentScore + 1).toString()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropUp,
                                    contentDescription = "Povećaj ocenu",
                                    tint = Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = onClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = mainColor,
                                contentColor = Color.Black,
                                disabledContainerColor = Color.Gray,
                                disabledContentColor = Color.White,
                            )
                        ) {
                            if (isLoading.value) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "Potvrdi",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                        Text(
                            text = "Izadji",
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    showScoreScreen.value = false
                                    isLoading.value = false
                                },
                            style = TextStyle(
                                fontSize = 20.sp,
                                color = greyTextColor,
                                textAlign = TextAlign.Center
                            )
                        )
                    }
                }
            }
        }
    )
}

