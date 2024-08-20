package com.example.sportfields.screens

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
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
fun RegisterScreen(navController: NavController, viewModel: LoginViewModel){
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val fullName = remember { mutableStateOf("") }
    val phoneNumber = remember { mutableStateOf("") }
    val profileImage = remember { mutableStateOf(Uri.EMPTY) }

    val isEmailError = remember { mutableStateOf(false) }
    val emailErrorText = remember { mutableStateOf("") }

    val isPasswordError = remember { mutableStateOf(false) }
    val passwordErrorText = remember { mutableStateOf("") }

    val isImageError = remember { mutableStateOf(false) }
    val isFullNameError = remember { mutableStateOf(false) }
    val isPhoneNumberError = remember { mutableStateOf(false) }

    val isError = remember { mutableStateOf(false) }
    val errorText = remember { mutableStateOf("") }

    val buttonIsEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }

    val registerFlow = viewModel.registerFlow.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(20.dp)
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 120.dp)
        ){
            Spacer(modifier = Modifier.height(40.dp))
            Heading1Text(textValue = "Pronadji sportski teren")
            Spacer(modifier = Modifier.height(5.dp))
            GreyText(textValue = "Napravi nalog")
            Spacer(modifier = Modifier.height(20.dp))
            LabelForInput(textValue = "Vasa slika")
            UploadIcon(profileImage, isImageError)
            Spacer(modifier = Modifier.height(10.dp))
            LabelForInput(textValue = "Ime i Prezime")
            Spacer(modifier = Modifier.height(10.dp))
            TextInput(
                isEmail = false,
                inputValue = fullName,
                inputText = "Dusan Pavlovic",
                leadingIcon = Icons.Outlined.Person,
                isError = isFullNameError,
                errorText = emailErrorText
            )
            Spacer(modifier = Modifier.height(10.dp))
            LabelForInput(textValue = "Email adresa")
            Spacer(modifier = Modifier.height(10.dp))
            TextInput(
                isEmail = false,
                inputValue = email,
                inputText = "email@gmail.com",
                leadingIcon = Icons.Outlined.MailOutline,
                isError = isEmailError,
                errorText = emailErrorText
            )
            Spacer(modifier = Modifier.height(10.dp))
            LabelForInput(textValue = "Broj telefona")
            Spacer(modifier = Modifier.height(10.dp))
            TextInput(
                isEmail = false,
                inputValue = phoneNumber,
                inputText = "0606227726",
                leadingIcon = Icons.Outlined.Phone,
                isError = isPhoneNumberError,
                errorText = emailErrorText
            )
            Spacer(modifier = Modifier.height(10.dp))
            LabelForInput(textValue = "Lozinka")
            Spacer(modifier = Modifier.height(10.dp))
            PasswordInput(
                inputValue = password,
                inputText = "********",
                leadingIcon = Icons.Outlined.Lock,
                isError = isPasswordError,
                errorText = passwordErrorText
            )
            Spacer(modifier = Modifier.height(10.dp))
            LoginRegisterButton(
                buttonText = "Registrujte se",
                icon = Icons.Filled.Login,
                isEnabled = buttonIsEnabled,
                isLoading = isLoading,
                onClick = {
                    isImageError.value = false
                    isEmailError.value = false
                    isPasswordError.value = false
                    isImageError.value = false
                    isFullNameError.value = false
                    isPhoneNumberError.value = false
                    isError.value = false
                    isLoading.value = true

                    if(profileImage.value == Uri.EMPTY && profileImage.value != null){
                        isImageError.value = true
                        isLoading.value = false
                    }else if(fullName.value.isEmpty()){
                        isFullNameError.value = true
                        isLoading.value = false
                    }else if(email.value.isEmpty()){
                        isEmailError.value = true
                        isLoading.value = false
                    }else if(phoneNumber.value.isEmpty()){
                        isPhoneNumberError.value = true
                        isLoading.value = false
                    }else if(password.value.isEmpty()){
                        isPasswordError.value = true
                        isLoading.value = false
                    }else {
                        viewModel.register(
                            profileImage = profileImage.value,
                            fullName = fullName.value,
                            email = email.value,
                            phoneNumber = phoneNumber.value,
                            password = password.value
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            customClickableText(firstText = "Vec imate nalog? ", secondText = "Prijavi se", onClick = {
                navController?.navigate(Routes.loginScreen)
            })

            registerFlow.value.let {
                when (it) {
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
                                emailErrorText.value = "Nevalidan format email adrese"
                            }

                            AuthenticationExceptions.invalidCredential -> {
                                isError.value = true
                                errorText.value = "Nesipravni podaci"
                            }

                            AuthenticationExceptions.shortPassword -> {
                                isPasswordError.value = true
                                passwordErrorText.value = "kratka lozinka (min = 6)"
                            }

                            AuthenticationExceptions.emailUsed -> {
                                isError.value = true
                                errorText.value = "Uneta email adresa je vec iskoriscena"
                            }

                            else -> {}
                        }
                    }
                    is Resource.Success -> {
                        isLoading.value = false
                        LaunchedEffect(Unit) {
                            navController?.navigate(Routes.firstScreen) {
                                popUpTo(Routes.firstScreen) {
                                    inclusive = true
                                }
                            }
                        }
                    }
                    is Resource.loading -> {

                    }
                    null -> {

                    }
                }
            }
        }
    }
}