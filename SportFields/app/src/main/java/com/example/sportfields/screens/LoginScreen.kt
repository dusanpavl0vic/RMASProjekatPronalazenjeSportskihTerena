package com.example.sportfields.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sportfields.exceptions.AuthenticationExceptions
import com.example.sportfields.navigation.Routes
import com.example.sportfields.repositories.Resource
import com.example.sportfields.viewmodels.LoginViewModel


@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel){

    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val isError = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }

    val buttonIsEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }

    val loginFlow = viewModel?.loginFlow?.collectAsState()

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)
        .padding(20.dp)) {
        Heading1Text(textValue = "Dobrodosli")
        Spacer(modifier = Modifier.height(20.dp))
        LabelForInput(textValue = "Unesite email")
        Spacer(modifier = Modifier.height(10.dp))
        TextInput(
            isEmail = true,
            inputValue = email,
            inputText = "email@gmail.com",
            leadingIcon = Icons.Outlined.Email,
            isError = isEmailError,
            errorText = emailErrorText
        )
        Spacer(modifier = Modifier.height(20.dp))
        LabelForInput(textValue = "Unesite lozinku")
        Spacer(modifier = Modifier.height(10.dp))
        PasswordInput(
            inputValue = password,
            inputText = "********",
            leadingIcon = Icons.Outlined.Lock,
            isError = isPasswordError,
            errorText = passwordErrorText
        )
        if(isError.value) customErrorContainer(errorText = "Neispravni podaci")
        Spacer(modifier = Modifier.height(50.dp))
        LoginRegisterButton(
            buttonText = "Prijavi se",
            icon = Icons.Filled.Login,
            isEnabled = buttonIsEnabled,
            isLoading = isLoading,
            onClick = {
                isEmailError.value = false
                isPasswordError.value = false
                isError.value = false
                isLoading.value = true
                viewModel.login(email.value, password.value)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        customClickableText(firstText = "Ako nemate nalog", secondText = "Registruj se", onClick = {
            navController.navigate(Routes.registerScreen)
        })
    }

    loginFlow?.value.let {
        when(it){
            is Resource.Failure -> {
                isLoading.value = false
                Log.d("Error", it.exception.message.toString())
                when (it.exception.message.toString()) {
                    AuthenticationExceptions.emptyFields -> {
                        isEmailError.value = true
                        isPasswordError.value = true
                    }
                    AuthenticationExceptions.badlyEmailFormat -> {
                        isEmailError.value = true
                        emailErrorText.value = "Neispravan email"
                    }
                    AuthenticationExceptions.invalidCredential -> {
                        isError.value = true
                        errorText.value = "Lozinka ili email nisu tacni"
                    }
                    else -> {}
                }

            }
            is Resource.Success -> {
                isLoading.value = false
                LaunchedEffect(Unit) {
                    navController.navigate(Routes.firstScreen) {
                        popUpTo(Routes.firstScreen) {
                            inclusive = true
                        }
                    }
                }
                Log.d("Login", "Logovan sam")
            }
            is Resource.loading -> {}
            null -> Log.d("Login", "nisi")
        }
    }
}