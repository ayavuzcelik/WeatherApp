package com.adm.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.adm.weatherapp.ui.theme.WeatherAppTheme
import com.adm.weatherapp.view.WeatherScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            WeatherAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = "weather_screen") {
                    composable("weather_screen") {
                        WeatherScreen(navController)
                    }
                }
            }
        }
    }
}
