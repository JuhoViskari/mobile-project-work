package com.example.weatherproject
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.weatherproject.combosables.LocationSearcher
import com.example.weatherproject.ui.theme.WeatherprojectTheme
import androidx.navigation.compose.rememberNavController
import com.example.weatherproject.combosables.ForecastScreen
import com.example.weatherproject.combosables.TemperatureScreen


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            WeatherprojectTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color.Red, Color.Blue)
                                )
                            )
                    ) {
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = "temperatureScreen"
                        ) {
                            composable("temperatureScreen") {
                                LocationSearcher { latitude, longitude ->
                                    TemperatureScreen(
                                        navController,
                                        latitude,
                                        longitude
                                    )
                                }
                            }
                            composable("forecastScreen") {
                                LocationSearcher { latitude, longitude ->
                                    ForecastScreen(
                                        navController,
                                        latitude,
                                        longitude
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}





