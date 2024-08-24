package com.example.sportfields.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sportfields.services.LocationService
import com.example.sportfields.ui.theme.lightGreenColor
import com.example.sportfields.ui.theme.mainColor

@Composable
fun SettingsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val isFollowingServiceEnabled = sharedPreferences.getBoolean("following_location", true)

    val checked = remember {
        mutableStateOf(isFollowingServiceEnabled)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(
                        mainColor,
                        RoundedCornerShape(15.dp)
                    )
                    .clip(RoundedCornerShape(15.dp))
                    .padding(10.dp)
            ) {


                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 30.dp, horizontal = 20.dp)
                ) {
                    BackButton {
                        navController?.popBackStack()
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Settings",
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Color.White,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 20.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Pretrazi terene u okolini",
                            style = TextStyle(
                                fontSize = 10.sp
                            )
                        )
                        Switch(
                            checked = checked.value,
                            onCheckedChange = {
                                checked.value = it
                                if (it) {
                                    Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_FIND_NEARBY
                                        context.startForegroundService(this)
                                    }
                                    with(sharedPreferences.edit()) {
                                        putBoolean("following_location", true)
                                        apply()
                                    }
                                } else {
                                    Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_STOP
                                        context.stopService(this)
                                    }
                                    //TODO: proveri nece nesto
                                    /*Intent(context, LocationService::class.java).apply {
                                        action = LocationService.ACTION_START
                                        context.startForegroundService(this)
                                    }*/
                                    with(sharedPreferences.edit()) {
                                        putBoolean("following_location", false)
                                        apply()
                                    }
                                }
                            },
                            thumbContent = if (checked.value) {
                                {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            } else {
                                null
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Green,
                                checkedTrackColor = lightGreenColor,
                                uncheckedThumbColor = Color.Red,
                                uncheckedTrackColor = Color.White,
                            )
                        )
                    }
                }
            }
        }
    }
}
