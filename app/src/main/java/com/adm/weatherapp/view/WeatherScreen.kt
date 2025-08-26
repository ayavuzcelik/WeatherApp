package com.adm.weatherapp.view

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.adm.weatherapp.model.GetWeatherResponse
import com.adm.weatherapp.ui.components.PermissionRequester
import com.adm.weatherapp.viewmodel.WeatherViewModel
import com.google.android.gms.location.LocationServices
import com.adm.weatherapp.R


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
    val main = weatherData.main
    val wind = weatherData.wind
    val clouds = weatherData.clouds
    val weatherCondition = weatherData.weather.firstOrNull()?.main ?: "Clear"
    val city = weatherData.name
    val backgroundRes = when (weatherCondition) {
        "Clear" -> R.drawable.bg_clear
        "Clouds" -> R.drawable.bg_clouds
        "Rain" -> R.drawable.bg_rain
        "Snow" -> R.drawable.bg_snow
        else -> R.drawable.bg_clear
    }

    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = backgroundRes),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = city,
                color = Color.White,
                fontSize = 48.sp
            )
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "${main.temp.toInt()}°C",
                color = Color.White,
                fontSize = 48.sp
            )

            Text(
                text = "Feels like ${main.feels_like.toInt()}°C",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                WeatherInfoCard(icon = R.drawable.ic_wind, label = "Wind", value = "${wind.speed} m/s")
                WeatherInfoCard(icon = R.drawable.ic_cloud, label = "Clouds", value = "${clouds.all}%")
                WeatherInfoCard(icon = R.drawable.ic_humidity, label = "Humidity", value = "${main.humidity}%")
                WeatherInfoCard(icon = R.drawable.ic_pressure, label = "Pressure", value = "${main.pressure} hPa")
            }
        }
    }
}

@Composable
fun WeatherInfoCard(icon: Int, label: String, value: String) {
    Card(
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, color = Color.White, fontSize = 14.sp)
        }
    }
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
