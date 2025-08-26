package com.adm.weatherapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adm.weatherapp.model.GetWeatherResponse
import com.adm.weatherapp.ui.components.PermissionRequester
import com.adm.weatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationServices

@Composable
fun WeatherScreen(
    navController: NavController,
    viewModel: WeatherViewModel = hiltViewModel(),
) {
    val weatherData by viewModel.weatherData
    val errorMessage by viewModel.errorMessage
    val isLoading by viewModel.isLoading
    val context = LocalContext.current
    val (lat, setLat) = remember { mutableStateOf<Double?>(null) }
    val (lon, setLon) = remember { mutableStateOf<Double?>(null) }


    PermissionRequester(
        permission = Manifest.permission.ACCESS_FINE_LOCATION,
        rationaleMessage = "To display the weather, we need location permission.",
        onPermissionGranted = {
            LaunchedEffect(Unit) {
                try {
                    val fusedLocationClient =
                        LocationServices.getFusedLocationProviderClient(context)
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            location?.let {
                                setLat(it.latitude)
                                setLon(it.longitude)
                                viewModel.saveLocation(it.latitude, it.longitude)
                                viewModel.fetchWeather(
                                    it.latitude.toString(),
                                    it.longitude.toString()
                                )
                            }

                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed to get location", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            Surface(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    errorMessage.isNotEmpty() -> {
                        RetryView(error = errorMessage) {
                            if (lat != null && lon != null) {
                                viewModel.fetchWeather(lat.toString(), lon.toString())
                            } else {
                                Toast.makeText(
                                    context,
                                    "Location not available",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }

                    weatherData != null -> {
                        WeatherData(weatherData = weatherData!!)
                    }
                }
            }
        },
        onPermissionDenied = { openSettings ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "Location permission denied. Weather cannot be displayed.",
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { openSettings() }) {
                        Text("Open Settings")
                    }
                }
            }
        },
        onPermissionError = {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("An error occurred, please try again.")
            }
        }

    )
}


@Composable
fun WeatherData(weatherData: GetWeatherResponse, modifier: Modifier = Modifier) {
    Text(
        text = weatherData.toString(),
        modifier = modifier
    )
}

@Composable
fun RetryView(
    error: String,
    onRetry: () -> Unit
) {
    Column {
        Text(error, color = Color.Red, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}
