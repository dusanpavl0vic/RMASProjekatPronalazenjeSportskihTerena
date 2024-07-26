package com.example.sportfields.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sportfields.screens.HomeScreen
import com.example.sportfields.screens.LoginScreen
import com.example.sportfields.screens.RegisterScreen
import com.example.sportfields.viewmodels.LoginViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router(loginViewModel : LoginViewModel){
    val navController = rememberNavController();
    NavHost(navController = navController, startDestination = Routes.homeScreen){
        composable(Routes.homeScreen){
            HomeScreen(navController = navController)
        }
        composable(Routes.loginScreen){
            LoginScreen(navController = navController, loginViewModel)
        }
        composable(Routes.registerScreen){
            RegisterScreen(navController = navController, loginViewModel)
        }
    }
}

object Routes {
    val homeScreen = "homeScreen"
    val loginScreen = "loginScreen"
    val registerScreen = "registerScreen"
}