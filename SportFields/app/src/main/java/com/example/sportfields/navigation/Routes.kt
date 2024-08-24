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
import com.example.sportfields.models.User
import com.example.sportfields.repositories.Resource
import com.example.sportfields.screens.AllFieldsScreen
import com.example.sportfields.screens.FieldScreen
import com.example.sportfields.screens.FirstScreen
import com.example.sportfields.screens.HomeScreen
import com.example.sportfields.screens.LoginScreen
import com.example.sportfields.screens.ProfileScreen
import com.example.sportfields.screens.RangListScreen
import com.example.sportfields.screens.RegisterScreen
import com.example.sportfields.screens.SettingsScreen
import com.example.sportfields.screens.addFieldScreen
import com.example.sportfields.viewmodels.FieldViewModel
import com.example.sportfields.viewmodels.LoginViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.rememberCameraPositionState

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
            route = Routes.firstScreen + "/{camera}/{latitude}/{longitude}/{fields}",
            arguments = listOf(
                navArgument("camera") { type = NavType.BoolType },
                navArgument("latitude") { type = NavType.FloatType },
                navArgument("longitude") { type = NavType.FloatType },
                navArgument("fields") { type = NavType.StringType }
            )
        ){
                backStackEntry ->
            val camera = backStackEntry.arguments?.getBoolean("camera")
            val latitude = backStackEntry.arguments?.getFloat("latitude")
            val longitude = backStackEntry.arguments?.getFloat("longitude")
            val fieldsJson = backStackEntry.arguments?.getString("fields")
            val fields = Gson().fromJson(fieldsJson, Array<Field>::class.java).toList()

            FirstScreen(
                navController = navController,
                viewModel = loginViewModel,
                fieldViewModel = fieldViewModel,
                fieldsMarkers = fields.toMutableList(),
                isCameraSet = remember {
                    mutableStateOf(camera!!)
                },
                cameraPosition = rememberCameraPositionState{
                    position = CameraPosition.fromLatLngZoom(LatLng(latitude!!.toDouble(), longitude!!.toDouble()), 17f)
                }
            )
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

        composable(
            route = Routes.profileScreen + "/{user}",
            arguments = listOf(navArgument("user"){
                type = NavType.StringType
            })
        ){
                backStackEntry ->
            val userDataJson = backStackEntry.arguments?.getString("user")
            val userData = Gson().fromJson(userDataJson, User::class.java)

            ProfileScreen(
                navController = navController,
                fieldViewModel = fieldViewModel,
                user = userData
            )
        }
        composable(
            route = Routes.fieldScreen + "/{field}",
            arguments = listOf(
                navArgument("field"){ type = NavType.StringType }
            )
        ){
                backStackEntry ->
            val fieldJson = backStackEntry.arguments?.getString("field")
            val field = Gson().fromJson(fieldJson, Field::class.java)
            fieldViewModel.getFieldScore(field.id)
            FieldScreen(
                navController = navController,
                fieldViewModel = fieldViewModel,
                loginViewModel = loginViewModel,
                field = field,
                fields = null
            )
        }
        composable(
            route = Routes.fieldScreen + "/{field}/{fields}",
            arguments = listOf(
                navArgument("field"){ type = NavType.StringType },
                navArgument("fields"){ type = NavType.StringType },
            )
        ){
                backStackEntry ->
            val fieldsJson = backStackEntry.arguments?.getString("fields")
            val fields = Gson().fromJson(fieldsJson, Array<Field>::class.java).toList()
            val fieldJson = backStackEntry.arguments?.getString("field")
            val field = Gson().fromJson(fieldJson, Field::class.java)

            fieldViewModel.getFieldScore(field.id)

            FieldScreen(
                navController = navController,
                fieldViewModel = fieldViewModel,
                loginViewModel = loginViewModel,
                field = field,
                fields = fields.toMutableList()
            )
        }
        composable(
            route = Routes.allFieldsScreen + "/{fields}",
            arguments = listOf(navArgument("fields") { type = NavType.StringType })
        ){
                backStackEntry ->
            val fieldsJson = backStackEntry.arguments?.getString("fields")
            val fields = Gson().fromJson(fieldsJson, Array<Field>::class.java).toList()
            AllFieldsScreen(fields = fields, navController = navController, fieldViewModel = fieldViewModel)
        }
        composable(Routes.settingsScreen){
            SettingsScreen(navController = navController)
        }
        composable(Routes.rangListScreen){
            RangListScreen(viewModel = loginViewModel, navController = navController)
        }
    }
}

object Routes {
    val homeScreen = "homeScreen"
    val loginScreen = "loginScreen"
    val registerScreen = "registerScreen"
    val addFieldScreen = "addFieldScreen"
    val firstScreen = "firstScreen"
    val profileScreen = "profileScreen"
    val fieldScreen = "fieldScreen"
    val allFieldsScreen = "allFieldsScreen"
    val settingsScreen = "settingsScreen"
    val rangListScreen = "rangListScreen"
}