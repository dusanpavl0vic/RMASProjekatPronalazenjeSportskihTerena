package com.example.sportfields.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.sportfields.models.User
import com.example.sportfields.repositories.Resource
import com.example.sportfields.ui.theme.lightGreenColor
import com.example.sportfields.viewmodels.LoginViewModel

@Composable
fun RangListScreen(
    viewModel: LoginViewModel?,
    navController: NavController?
) {
    viewModel?.getAllUserData()
    val allUsersResource = viewModel?.allUsers?.collectAsState()

    val allUsers = remember {
        mutableListOf<User>()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(12.dp, 20.dp, 20.dp, 20.dp)
    ) {
        if (allUsers.isNotEmpty()) {
            Column {
                BackButton {
                    navController?.popBackStack()
                }

                Spacer(modifier = Modifier.height(20.dp))

                Heading1Text(textValue = "Top 3 Ranking")

                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            lightGreenColor,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Top, // Align items to the top
                        horizontalAlignment = Alignment.CenterHorizontally // Center align items horizontally
                    ) {
                        if (allUsers.isNotEmpty()) {
                            allUsers.take(3).forEachIndexed { index, user ->
                                val color = when (index) {
                                    0 -> Color(0xFFFFD700) // Gold color for first place
                                    1 -> Color(0xFFC0C0C0) // Silver color for second place
                                    2 -> Color(0xFFCD7F32) // Bronze color for third place
                                    else -> Color.Gray
                                }

                                // Circle with index number
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(color, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = (index + 1).toString(),
                                        color = Color.White,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp) // Add padding around text
                                ) {
                                    Text(
                                        text = user.fullName,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "Score: ${user.score}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Light,
                                        color = Color.DarkGray
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp)) // Add space between users
                            }

                            if (allUsers.size > 3) {
                                val otherUsers = allUsers.drop(3)

                                Column {
                                    otherUsers.forEachIndexed { index, user ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = "${index + 4}. ${user.fullName}",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Normal,
                                                color = Color.Black
                                            )
                                            Text(
                                                text = "Score: ${user.score}",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Light,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }

        }
    }

    allUsersResource?.value.let {
        when (it) {
            is Resource.Failure -> {}
            is Resource.Success -> {
                allUsers.clear()
                allUsers.addAll(it.result.sortedByDescending { x -> x.score })
            }
            Resource.loading -> {}
            null -> {}
        }
    }
}

