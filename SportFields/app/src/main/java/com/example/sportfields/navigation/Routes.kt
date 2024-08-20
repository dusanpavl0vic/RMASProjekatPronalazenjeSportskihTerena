package com.example.sportfields.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.sportfields.models.Field
import com.example.sportfields.repositories.Resource
import com.example.sportfields.screens.FirstScreen
import com.example.sportfields.screens.HomeScreen
import com.example.sportfields.screens.LoginScreen
import com.example.sportfields.screens.RegisterScreen
import com.example.sportfields.screens.addFieldScreen
import com.example.sportfields.viewmodels.FieldViewModel
import com.example.sportfields.viewmodels.LoginViewModel
import com.google.android.gms.maps.model.LatLng

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Router(loginViewModel : LoginViewModel, fieldViewModel : FieldViewModel){
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
        composable(Routes.firstScreen){
            Log.d("Uso", "usooooo")
            val fieldsData = fieldViewModel.fields.collectAsState()
            val fieldsPins = remember {
                mutableListOf<Field>()
            }
            fieldsData.value.let {
                when(it){
                    is Resource.Success -> {
                        fieldsPins.clear()
                        fieldsPins.addAll(it.result)
                    }
                    is Resource.loading -> {
                    }
                    is Resource.Failure -> {
                        Log.e("Podaci", it.toString())
                    }
                    null -> { Log.d("FirstScreen", "nestooooo")}
                }
            }
            Log.d("Lokacije", fieldsPins.toString())
            FirstScreen(navController = navController, viewModel = loginViewModel, fieldViewModel = fieldViewModel, fieldsMarkers = fieldsPins)
        }

        composable(
            Routes.addFieldScreen+"/{latitude}/{longitude}",
            arguments = listOf(
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType }
            )
        ){ backStackEntry ->
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")

            val location = remember {
                mutableStateOf(LatLng(latitude!!.toDouble(), longitude!!.toDouble()))
            }
            addFieldScreen(fieldViewModel = fieldViewModel, location = location, navController)
        }
    }
}

object Routes {
    val homeScreen = "homeScreen"
    val loginScreen = "loginScreen"
    val registerScreen = "registerScreen"
    val addFieldScreen = "addFieldScreen"
    val firstScreen = "firstScreen"
}