package com.example.sportfields.screens

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import com.example.sportfields.R
import com.example.sportfields.models.Field
import com.example.sportfields.models.User
import com.example.sportfields.navigation.Routes
import com.example.sportfields.repositories.Resource
import com.example.sportfields.services.LocationService
import com.example.sportfields.ui.theme.mainColor
import com.example.sportfields.viewmodels.FieldViewModel
import com.example.sportfields.viewmodels.LoginViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FirstScreen(
    navController: NavController,
    viewModel: LoginViewModel,
    isCameraSet: MutableState<Boolean> = remember {
        mutableStateOf(false)
    },
    cameraPosition : CameraPositionState = rememberCameraPositionState(){
        position = CameraPosition.fromLatLngZoom(LatLng(43.321445, 21.896104), 17f)
    },
    fieldViewModel: FieldViewModel,
    fieldsMarkers: MutableList<Field>
){

    //Log.e("Svi markeri", fieldsMarkers.toString())
    //Log.d("FirstScreen", "Pocetak")
    viewModel.getUserData()
    val userDataResource = viewModel.currentUserFlow.collectAsState()
    val user = remember {
        mutableStateOf<User?>(null)
    }
    val markers = remember { mutableStateListOf<LatLng>() }
    val uiSettings = remember { mutableStateOf(MapUiSettings(zoomControlsEnabled = false)) }
    val properties = remember {
        mutableStateOf(MapProperties(mapType = MapType.TERRAIN))
    }


    val myLocation = remember {
        mutableStateOf<LatLng?>(null)
    }

    val buttonIsEnabled = remember { mutableStateOf(true) }
    val isLoading = remember { mutableStateOf(false) }

    val fieldsData = fieldViewModel.fields.collectAsState()
    val allFields = remember {
        mutableListOf<Field>()
    }

    //Log.d("FirstScreen", "Ovdeeeeeeeeee")


    fieldsData.value.let {
        when(it){
            is Resource.Success -> {
                allFields.clear()
                allFields.addAll(it.result)
            }
            is Resource.loading -> {

            }
            is Resource.Failure -> {
                Log.e("Podaci", it.toString())
            }
            null -> Log.e("Podaci", "doslo do greskee")
        }
    }

    //Log.d("Lokacija", "Nova1");
    val receiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("BroadcastReceiver", "onReceive called with action: ${intent?.action}")
                if (intent?.action == LocationService.ACTION_LOCATION_UPDATE) {
                    val latitude = intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LATITUDE, 0.0)
                    val longitude = intent.getDoubleExtra(LocationService.EXTRA_LOCATION_LONGITUDE, 0.0)
                    Log.d("BroadcastReceiver", "Location received: Lat=$latitude, Lon=$longitude")
                    myLocation.value = LatLng(latitude, longitude)
                    //Log.d("Nova lokacija", myLocation.toString())
                }
            }
        }
    }
    //Log.d("Lokacija", "Nova2");
    val context = LocalContext.current

    val filtersOn = remember {
        mutableStateOf(false)
    }
    val filteredFields = remember {
        mutableListOf<Field>()
    }

    DisposableEffect(context) {
        LocalBroadcastManager.getInstance(context)
            .registerReceiver(receiver, IntentFilter(LocationService.ACTION_LOCATION_UPDATE))
        onDispose {
            LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver)
        }
    }

    LaunchedEffect(myLocation.value) {
        myLocation.value?.let {
            if (!isCameraSet.value) {
                //Log.d("Nova lokacija", myLocation.toString())
                cameraPosition.position = CameraPosition.fromLatLngZoom(it, 17f)
                isCameraSet.value = true
            }
            markers.clear()
            markers.add(it)
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // State to control gesturesEnabled
    val gesturesEnabled = remember { mutableStateOf(false) }

    // Monitor the drawer state and update gesturesEnabled accordingly
    LaunchedEffect(drawerState.isOpen) {
        gesturesEnabled.value = drawerState.isOpen
    }

    //Log.e("Moja lokacija", myLocation.toString())

    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val isFilteredIndicator = remember{
        mutableStateOf(false)
    }

    var FieldsToShow = remember {
        mutableListOf<Field>()
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,

        sheetContent = {
            FilterScreen(
                viewModel = viewModel,
                fields = allFields,
                sheetState = sheetState,
                isFiltered = filtersOn,
                isFilteredIndicator = isFilteredIndicator,
                filteredField = filteredFields,
                fieldMarkers = fieldsMarkers,
                userLocation = myLocation.value
            )
        },
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        modifier = Modifier.fillMaxSize()
    ){
        Column(modifier = Modifier
            .fillMaxSize()
            .height(100.dp),)
        {
            ModalNavigationDrawer(
                drawerState = drawerState,
                gesturesEnabled = gesturesEnabled.value,
                drawerContent = {
                    ModalDrawerSheet {
                        Box(
                            modifier = androidx.compose.ui.Modifier
                                .background(mainColor)
                                .fillMaxWidth()
                                .height(140.dp)
                        ){
                            if(user.value != null)
                                UserImage(user.value!!.image, user.value!!.fullName, user.value!!.score)
                        }
                        HorizontalDivider()
                        NavigationDrawerItem(
                            label = { Text(text = "Profil", color = mainColor) },
                            selected = false,
                            icon = { Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "profile", tint = mainColor) },
                            onClick = {
                                coroutineScope.launch {
                                    drawerState.close()
                                    val userJson = Gson().toJson(user.value)
                                    val encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8.toString())
                                    navController.navigate(Routes.profileScreen + "/$encodedUserJson")
                                }
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "Mesta", color = mainColor) },
                            selected = false,
                            icon = { Icon(imageVector = Icons.Filled.Place, contentDescription = "Fields", tint = mainColor) },
                            onClick = {
                                //TODO: navigacija na prikaz terena
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "Rang lista", color = mainColor) },
                            selected = false,
                            icon = { Icon(imageVector = Icons.Filled.PeopleAlt, contentDescription = "list", tint = mainColor) },
                            onClick = {
                                //TODO: prikaz liste po ocenama
                            }
                        )
                        NavigationDrawerItem(
                            label = { Text(text = "Podesavanja", color = mainColor) },
                            selected = false,
                            icon = { Icon(imageVector = Icons.Filled.Settings, contentDescription = "settings", tint = mainColor) },
                            onClick = {
                                //TODO: navigacija na setting screen
                            }
                        )
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Box(modifier = Modifier.padding(10.dp)){
                                LoginRegisterButton(
                                    buttonText = "Odjavi se",
                                    icon = Icons.Filled.Logout,
                                    isEnabled = buttonIsEnabled,
                                    isLoading = isLoading
                                ) {
                                    viewModel.logout()
                                    navController.navigate(Routes.loginScreen)
                                }
                            }
                        }
                    }
                },
            ) {
                Box(modifier = Modifier.fillMaxSize()){
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPosition,
                        properties = properties.value,
                        uiSettings = uiSettings.value,
                    ){
                        markers.forEach { marker ->
                            val icon = myPositionIndicator(
                                context, R.drawable.my_location
                            )
                            Marker(
                                state = rememberMarkerState(position = marker),
                                title = "Moja Lokacija",
                                icon = icon,
                                snippet = "",
                            )
                        }
                        if(!filtersOn.value) {
                            fieldsMarkers.forEach { field ->
                                val iconResId = fieldTypeIcons[field.type] ?: R.drawable.sports
                                val icon = myPositionIndicator(
                                    context, iconResId
                                )
                                FieldMarker(
                                    field = field,
                                    icon = icon,
                                    fieldMarkers = fieldsMarkers,
                                    navController = navController,
                                    notFiltered = true
                                )
                            }
                        }
                        else{
                            Log.d("filtrirani tereni", filteredFields.map { x -> x.type }.toString())
                            filteredFields.forEach{ field ->
                                val iconResId = fieldTypeIcons[field.type] ?: R.drawable.sports
                                val icon = myPositionIndicator(
                                    context, iconResId
                                )
                                FieldMarker(
                                    field = field,
                                    icon = icon,
                                    fieldMarkers = fieldsMarkers,
                                    navController = navController,
                                    notFiltered = false
                                )
                            }
                        }

                    }
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.height(15.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .background(mainColor, shape = RoundedCornerShape(40.dp))
                                .padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        drawerState.open()
                                    }
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.acc),
                                    contentDescription = "Menu",
                                    tint = Color.White
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .background(Color.Transparent, shape = CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            IconButton(
                                onClick = {
                                    coroutineScope.launch {
                                        sheetState.show()
                                    }
                                },
                                modifier = Modifier.size(40.dp)
                            ) {
                                Icon(
                                    imageVector = ImageVector.vectorResource(id = R.drawable.filter),
                                    contentDescription = "Filter",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(15.dp))

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Bottom
                        ) {
                            Row {
                                Box(
                                    modifier = Modifier.background(
                                        mainColor,
                                        shape = CircleShape
                                    )
                                ) {
                                    IconButton(
                                        onClick = {
                                            navController.navigate(route = Routes.addFieldScreen + "/${myLocation.value!!.latitude}/${myLocation.value!!.longitude}")
                                        },
                                        modifier = Modifier.size(60.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Add",
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(15.dp))
                            }
                            Spacer(modifier = Modifier.height(15.dp))
                        }
                    }

                }
            }
        }
    }


    userDataResource.value.let {
        when(it){
            is Resource.Success -> {
                user.value = it.result
            }
            null -> {
                user.value = null
            }

            is Resource.Failure -> {}
            Resource.loading -> {}
        }
    }

}