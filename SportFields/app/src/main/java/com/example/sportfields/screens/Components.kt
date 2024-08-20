package com.example.sportfields.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.sportfields.R
import com.example.sportfields.models.Field
import com.example.sportfields.ui.theme.buttonDisabledColor
import com.example.sportfields.ui.theme.greyTextColor
import com.example.sportfields.ui.theme.mainColor
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState


@Composable
fun Heading1Text(textValue: String){
    Text(style = TextStyle(
        color = Color.Black,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
    ),
        modifier = Modifier.fillMaxWidth(),
        text = textValue
    )
}

@Composable
fun GreyText(textValue: String){
    Text(style = TextStyle(
        color = greyTextColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center
    ),
        modifier = Modifier.fillMaxWidth(),
        text = textValue
    )
}

@Composable
fun LabelForInput(textValue: String){
    Text(style = TextStyle(
        color = Color.Black,
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium
    ),
        text = textValue
    )
}

@Composable
fun SportFieldImage(){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(20.dp), contentAlignment = Alignment.Center){
        Image(painter = painterResource(id = R.drawable.sportfield),
            contentDescription = "Login Image",
            modifier = Modifier
                .width(210.dp)
                .height(210.dp)
        )
    }
}

@Composable
fun UploadIcon(
    selectedImageUri: MutableState<Uri?>,
    isError: MutableState<Boolean>

) {
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri.value = uri
        }
    )

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        if (selectedImageUri.value == Uri.EMPTY || selectedImageUri.value == null) {
            Image(
                painter = painterResource(id = R.drawable.plus_icon),
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(40.dp)
                    .border(
                        if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                            0.dp,
                            Color.Transparent
                        )
                    )
                    .clip(RoundedCornerShape(70.dp)) // 50% border radius
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            )
        } else {
            selectedImageUri.value?.let { uri ->
                Image(
                    painter = painterResource(id = R.drawable.plus_icon),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(40.dp)
                        .border(
                            if (isError.value) BorderStroke(2.dp, Color.Red) else BorderStroke(
                                0.dp,
                                Color.Transparent
                            )
                        )
                        .clip(RoundedCornerShape(70.dp)) // 50% border radius
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        }
                )

                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.LightGray)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
@Composable
fun TextInput(
    isEmail: Boolean,
    isNumber: Boolean = false,
    inputValue: MutableState<String>,
    inputText: String,
    leadingIcon: ImageVector,
    isError: MutableState<Boolean>,
    errorText: MutableState<String>
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .shadow(
                6.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isError.value) Color.Red else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                Color.White,
                shape = RoundedCornerShape(10.dp)
            )
    ){
        OutlinedTextField(
            value = inputValue.value,
            onValueChange = { newValue ->
                inputValue.value = newValue
                isError.value = false
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = inputText,
                    style = TextStyle(
                        color = greyTextColor,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            leadingIcon = {
                Icon(imageVector = leadingIcon,
                    contentDescription = "",
                    tint = Color.Black)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = if(isEmail && !isNumber) KeyboardOptions(keyboardType = KeyboardType.Email) else if(!isEmail && isNumber) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default
        )
    }
    if(isError.value && errorText.value.isNotEmpty()) {
        Text(
            text = errorText.value,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        )
    }
}

@Composable
fun PasswordInput(
    inputValue: MutableState<String>,
    inputText: String,
    leadingIcon: ImageVector,
    isError: MutableState<Boolean>,
    errorText: MutableState<String>
){
    var showPassword = remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .shadow(
                6.dp,
                shape = RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isError.value) Color.Red else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                Color.White,
                shape = RoundedCornerShape(10.dp)
            )
    ){
        OutlinedTextField(
            value = inputValue.value,
            onValueChange = { newValue ->
                inputValue.value = newValue
                isError.value = false
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = inputText,
                    style = TextStyle(
                        color = greyTextColor,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            leadingIcon = {
                Icon(imageVector = leadingIcon,
                    contentDescription = "",
                    tint = Color.Black)
            },
            trailingIcon = {
                IconButton(onClick = {
                    showPassword.value = !showPassword.value
                }) {
                    Icon(
                        imageVector = if(!showPassword.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            visualTransformation = if(!showPassword.value) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
    }
    if(isError.value && errorText.value.isNotEmpty()) {
        Text(
            text = errorText.value,
            modifier = Modifier.fillMaxWidth(),
            style = TextStyle(
                textAlign = TextAlign.Center,
                color = Color.Red
            )
        )
    }else{
        Text(text = " ")
    }
}

@Composable
fun LoginRegisterButton(
    buttonText: String,
    icon: ImageVector,
    isEnabled: MutableState<Boolean>,
    isLoading: MutableState<Boolean>,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        modifier = Modifier
            .padding(vertical = 2.dp)
            .height(50.dp)
            .border(2.dp, mainColor, RoundedCornerShape(10.dp))
        ,
        colors = ButtonDefaults.buttonColors(
            containerColor = mainColor,
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = buttonDisabledColor
        ),
        shape = RoundedCornerShape(10.dp),
        enabled = isEnabled.value

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            if(!isLoading.value){
                Icon(imageVector = icon, contentDescription = null)
                Spacer(modifier = Modifier.width(10.dp))
            }
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxHeight()
            ) {
                if (isLoading.value) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = mainColor,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = buttonText,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun customClickableText(
    firstText: String,
    secondText: String,
    onClick: () -> Unit
){
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = firstText,
            style = TextStyle(
                fontSize = 12.sp
            )
        )
        Text(
            text = secondText,
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .padding(start = 4.dp),
            style = TextStyle(
                fontSize = 12.sp,
                color = mainColor
            )
        )
    }
}

@Composable
fun customErrorContainer(
    errorText: String
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
        contentAlignment = Alignment.Center
    ){
        Text(
            text = errorText,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                color = Color.Red
            )
        )
    }
}

@Composable
fun UserImage(
    imageUrl: String,
    name: String,
    score: Int
){
    Box(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 20.dp), contentAlignment = Alignment.Center){
        Row {
            AsyncImage(
                model = imageUrl,
                contentDescription = "userimage",
                modifier = Modifier
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        3.dp,
                        Color.White,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .shadow(
                        6.dp,
                        shape = RoundedCornerShape(100.dp)
                    )
                    .clip(shape = RoundedCornerShape(100.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(40.dp))
            Column(
                modifier = Modifier
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    modifier = Modifier
                        .padding(10.dp),
                    text = name.replace(","," "),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(5.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(style = TextStyle(
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center),
                        text = "Bodovi:")
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        style = TextStyle(
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        ),
                        text = score.toString()
                    )
                }
            }
        }
    }
}

@Composable
fun FieldMarker(
    field: Field,
    icon: BitmapDescriptor?,
    fieldMarkers : MutableList<Field>,
    navController: NavController,
    notFiltered: Boolean
){
    Marker(
        state = if(notFiltered){
            rememberMarkerState(
                position = LatLng(
                    field.location.latitude,
                    field.location.longitude
                )
            )
        }
        else{
            MarkerState(
                position = LatLng(
                    field.location.latitude,
                    field.location.longitude
                )
            )
        }
        ,
        title = field.type,
        icon = icon,
        snippet = field.description,
        //onClick = {

            //TODO: navigacija na profil terena
        //}
    )
}
fun myPositionIndicator(
    context: Context,
    vectorResId: Int
): BitmapDescriptor? {

    // retrieve the actual drawable
    val drawable = ContextCompat.getDrawable(context, vectorResId) ?: return null
    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
    val bm = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )

    // draw it onto the bitmap
    val canvas = android.graphics.Canvas(bm)
    drawable.draw(canvas)
    return BitmapDescriptorFactory.fromBitmap(bm)
}

@Composable
fun GalleryForPlace(
    selectedImages: MutableState<List<Uri>>
) {
    val pickImagesLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        selectedImages.value += uris
    }

    LazyRow {
        if (selectedImages.value.size < 4) {
            item {
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .width(100.dp)
                        .height(100.dp)
                        .border(
                            1.dp,
                            mainColor,
                            shape = RoundedCornerShape(10.dp),
                        )
                        .clickable { pickImagesLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = "")
                }
            }
        }
        items(selectedImages.value.size) { index ->
            val uri = selectedImages.value[index]
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .width(100.dp)
                    .height(100.dp)
                    .border(
                        1.dp,
                        Color.Transparent,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .background(
                        Color.White,
                        shape = RoundedCornerShape(10.dp),
                    )
                    .clickable { selectedImages.value -= uri },
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(10.dp))
                )
            }
        }
    }
}
@Composable
fun SimpleTextInput(
    inputValue: MutableState<String>,
    inputText: String,
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .border(
                1.dp,
                Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                Color.White,
                shape = RoundedCornerShape(10.dp)
            )
    ){
        OutlinedTextField(
            value = inputValue.value,
            onValueChange = { newValue ->
                inputValue.value = newValue
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = inputText,
                    style = TextStyle(
                        color = greyTextColor,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            )
        )
    }
}
@Composable
fun TextArea(
    inputValue: MutableState<String>,
    inputText: String,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .border(
                1.dp,
                mainColor,
                shape = RoundedCornerShape(10.dp)
            )
            .background(
                Color.White,
                shape = RoundedCornerShape(10.dp)
            )
    ) {
        OutlinedTextField(
            value = inputValue.value,
            onValueChange = { newValue ->
                inputValue.value = newValue
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            placeholder = {
                Text(
                    text = inputText,
                    style = TextStyle(
                        color = greyTextColor,
                        fontWeight = FontWeight.Medium
                    )
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions.Default
        )
    }
}
@Composable
fun ImageLogo(
    selectedImageUri: MutableState<Uri?>
){
    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            selectedImageUri.value = uri
        }
    )

    val interactionSource = remember { MutableInteractionSource() }
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .border(
            1.dp,
            Color.Transparent,
            shape = RoundedCornerShape(20.dp)
        ),
        contentAlignment = Alignment.Center,
    ){
        if (selectedImageUri.value == Uri.EMPTY || selectedImageUri.value == null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        singlePhotoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.plus_icon),
                    modifier = Modifier
                        .width(100.dp)
                        .height(100.dp),
                    contentDescription = ""
                )
                Text(text = "Dodaj glavnu sliku")
            }
        }else{
            selectedImageUri.value?.let { uri ->
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier
                        .width(150.dp)
                        .height(150.dp)
                        .background(Color.LightGray)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            singlePhotoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FieldTypeDropdown(selectedType: FieldType, onTypeSelected: (FieldType) -> Unit) {
    var expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded.value,
        onExpandedChange = { expanded.value = !expanded.value }
    ) {
        TextField(
            value = selectedType.displayName,
            onValueChange = {},
            readOnly = true,
            label = { Text("Tip terena") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier.menuAnchor()
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        ) {
            FieldType.values().forEach { type ->
                DropdownMenuItem(
                    onClick = {
                        onTypeSelected(type)
                        expanded.value = false
                    }
                ) {
                    Text(text = type.displayName)
                }
            }
        }
    }
}



