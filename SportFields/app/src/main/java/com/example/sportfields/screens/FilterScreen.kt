package com.example.sportfields.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sportfields.models.Field
import com.example.sportfields.ui.theme.lightGray
import com.example.sportfields.ui.theme.lightGreenColor
import com.example.sportfields.ui.theme.mainColor
import com.example.sportfields.viewmodels.LoginViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
import java.math.RoundingMode
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun FilterScreen(
    viewModel: LoginViewModel,
    fields: MutableList<Field>,
    sheetState: ModalBottomSheetState,
    isFiltered: MutableState<Boolean>,
    isFilteredIndicator: MutableState<Boolean>,
    filteredField: MutableList<Field>,
    fieldMarkers: MutableList<Field>,
    userLocation: LatLng?
) {
    val context = LocalContext.current

    viewModel.getAllUserData()

    val allUsersNames = remember {
        mutableListOf<String>()
    }

    val sharedPreferences = context.getSharedPreferences("filters", Context.MODE_PRIVATE)
    val options = sharedPreferences.getString("options", null)
    val range = sharedPreferences.getFloat("range", 1000f)

    val initialCheckedState = remember {
        mutableStateOf(List(allUsersNames.size) { false })
    }
    val rangeValues = remember { mutableFloatStateOf(1000f) }
    
    val filtersSet = remember {
        mutableStateOf(false)
    }
    
    if (isFilteredIndicator.value && options != null) {
        val type = object : TypeToken<List<Boolean>>() {}.type
        val savedOptions: List<Boolean> = Gson().fromJson(options, type) ?: emptyList()
        initialCheckedState.value = savedOptions
    }
    if(!filtersSet.value) {
        if (isFilteredIndicator.value) {
            rangeValues.floatValue = range
        }
        filtersSet.value = true
    }



    val selectedOption = remember {
        mutableStateOf("")
    }



   
    val coroutineScope = rememberCoroutineScope()

    val expanded = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 16.dp)
    ) {
        Text(
            text = "Tip terena",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))



        ExposedDropdownMenuBox(
            expanded = expanded.value,
            onExpandedChange = { expanded.value = !expanded.value }
        ) {
            TextField(
                value = selectedOption.value,
                onValueChange = {},
                readOnly = true,
                label = { Text("Izaberi tip terena") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded.value)
                },
                colors = ExposedDropdownMenuDefaults.textFieldColors(),
                modifier = Modifier.menuAnchor()
                    .fillMaxWidth()
                    .background(lightGray, RoundedCornerShape(4.dp))
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            )

            DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                FieldType.values().forEach { option ->
                    DropdownMenuItem(
                        onClick = {
                            selectedOption.value = option.toString()
                            expanded.value = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(option.toString())
                    }
                }
            }
        }



        Spacer(modifier = Modifier.height(8.dp))
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Udaljenost",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text =
                if(rangeValues.floatValue != 1000f)
                    rangeValues.floatValue.toBigDecimal().setScale(1, RoundingMode.UP).toString() + "m"
                else
                    "∞"
                ,style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        RangeSliderExample(rangeValues = rangeValues)
        Spacer(modifier = Modifier.height(30.dp))
        CustomFilterButton {
            fieldMarkers.clear()
            val filteredFields = fields.toMutableList()

            if (rangeValues.floatValue != 1000f) {
                filteredFields.retainAll { field ->
                    calculateDistance(
                        userLocation!!.latitude,
                        userLocation.longitude,
                        field.location.latitude,
                        field.location.longitude
                    ) <= rangeValues.floatValue
                }
                with(sharedPreferences.edit()) {
                    putFloat("range", rangeValues.floatValue)
                    apply()
                }
            }


            if(selectedOption.value != "") {
                selectedOption.value?.let { selectedFieldType ->
                    filteredFields.retainAll { field ->
                        field.type == selectedFieldType.toString()
                    }

                    with(sharedPreferences.edit()) {
                        putString("selected_option", selectedFieldType.toString())
                        apply()
                    }
                }
            }

            filteredField.clear()
            filteredField.addAll(filteredFields)

            isFiltered.value = false
            isFiltered.value = true

            coroutineScope.launch {
                sheetState.hide()
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        CustomResetFilters {
            fieldMarkers.clear()
            fieldMarkers.addAll(fields)

            initialCheckedState.value =
                List(allUsersNames.count()) { false }.toMutableList()
            rangeValues.floatValue = 1000f

            isFiltered.value = true
            isFiltered.value = false
            isFilteredIndicator.value = false

            with(sharedPreferences.edit()) {
                putFloat("range", 1000f)
                putString("options", null)
                apply()
            }

            coroutineScope.launch {
                sheetState.hide()
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}



@Composable
fun RangeSliderExample(
    rangeValues: MutableState<Float>
) {
    androidx.compose.material3.Slider(
        value = rangeValues.value,
        onValueChange = { rangeValues.value = it },
        valueRange = 0f..1000f,
        steps = 50,
        colors = SliderDefaults.colors(
            thumbColor = mainColor,
            activeTrackColor = lightGreenColor,
            inactiveTrackColor = lightGreenColor
        )
    )
}

@Composable
fun CustomFilterButton(
    onClick: () -> Unit
){
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(mainColor, RoundedCornerShape(30.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = mainColor,
            contentColor = Color.White,
            disabledContainerColor = lightGreenColor,
            disabledContentColor = Color.White
        ),

        ) {
        Text(
            "Filtriraj",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

@Composable
fun CustomResetFilters(
    onClick: () -> Unit
){
    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(mainColor, RoundedCornerShape(30.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = mainColor,
            contentColor = Color.White,
            disabledContainerColor = lightGreenColor,
            disabledContentColor = Color.White
        ),

        ) {
        Text(
            "Resetuj Filtere",
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        )
    }
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371000.0

    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)

    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)

    val c = 2 * atan2(sqrt(a), sqrt(1 - a))

    return earthRadius * c
}