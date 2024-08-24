package com.example.sportfields.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.sportfields.R
import com.example.sportfields.navigation.Routes
import com.example.sportfields.ui.theme.mainColor

@Composable
fun HomeScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 16.dp)
        )

        Text(
            text = "SportFields",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = mainColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            text = "Pronadjite teren za sportske aktivnosti",
            fontSize = 16.sp,
            color = mainColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )


        Button(
            onClick = { navController.navigate(Routes.loginScreen) },
            colors = ButtonDefaults.buttonColors(
                containerColor = mainColor,
                contentColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Uloguj se")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ClickableText(
            text = AnnotatedString("Ako nemate nalog? Registruj se"),
            onClick = { navController.navigate(Routes.registerScreen) },
            style = LocalTextStyle.current.copy(color = mainColor, fontSize = 16.sp)
        )
    }
}