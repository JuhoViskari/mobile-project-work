package com.example.weatherproject.combosables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherproject.DataClasses.ApiResponse
import com.example.weatherproject.DataClasses.fetchWeatherData
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
/**
 * Showing current temperature information, possibility to click button to other screen
 * to check 7 day forecast
 *
 * This Screen search forecast information by given location and show it to user
 * Using [latitude] and [longitude] parameters to specify locations
 *
 * @param navController which is navigation parameter
 * @param latitude which used by forecast to specify latitude location
 * @param longitude which used by forecast to specify longitude location
 *
 * @author Juho Viskari
 */
@Composable
fun TemperatureScreen(navController: NavController, latitude: Double, longitude: Double) {


    val coroutineScope = rememberCoroutineScope()

    var weatherResponse by remember {
        mutableStateOf<ApiResponse?>(null)
    }
    // show error false mutablestate for fetching data errors
    var showError by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val response = fetchWeatherData(latitude, longitude)
            weatherResponse = response

            // if response is null show error
            if (response == null) {
                showError = true
            }
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // if response is null and showError is not false show loading screen
        if (weatherResponse == null && !showError) {
            // Show loading indicator when
            CircularProgressIndicator()
            // else if show error false show text
        }else if (showError) {
                Text(text = "Ei verkkoyhteyttä, tarkista, että datayhteys on käytettävissä ja yritä uudelleen",
                    textAlign = TextAlign.Center)
            // else show temperature and button
            } else {

                val currentTemperature = weatherResponse?.current?.temperature_2m ?: 0.0
                val roundedTemperature = currentTemperature.roundToInt()
                val weatherCode = weatherResponse?.current?.weather_code ?: ""

                val weatherDescriptionEmoji = when (weatherCode.toIntOrNull()) {
                    0 -> "☀️"
                    1 -> "\uD83C\uDF24"
                    2 -> "\uD83C\uDF25"
                    3 -> "\uD83C\uDF25"
                    in 45..48 -> "☁️"
                    in 51..57 -> "\uD83C\uDF26"
                    in 61..67 -> "\uD83C\uDF27"
                    in 71..77 -> "\uD83C\uDF28"
                    80, 81, 82 -> "\uD83C\uDF27"
                    85, 86 -> "\uD83C\uDF28"
                    95 -> "\uD83C\uDF29"
                    96, 99 -> "\uD83C\uDF29"
                    else -> "\uD83D\uDC80"
                }
                val weatherDescription = when (weatherCode.toIntOrNull()) {
                    0 -> "Selkeää"
                    1 -> "Pääosin selkeää"
                    2 -> "Puolipilvistä"
                    3 -> "Pilvistä"
                    in 45..48 -> "Sumua tai kerrostavaa kuurasumua"
                    in 51..57 -> "Tihkusadetta tai jäätävää tihkua"
                    in 61..67 -> "Sadetta tai jäätävää sadetta"
                    in 71..77 -> "Lumisadetta tai rakeita"
                    80, 81, 82 -> "Sadekuuroja"
                    85, 86 -> "Lumikuuroja"
                    95 -> "Kevyttä tai kohtalaista ukkosta"
                    96, 99 -> "ukkosmyrsky: lievästi tai voimakkaasti rakeista"
                    else -> "Tuntematon sääkoodi"
                }

                // UI for frontpage
                Card(
                    modifier = Modifier.padding(16.dp),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            text = "Lämpötila: $roundedTemperature °C",
                            fontSize = 20.sp
                        )

                        Text(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            text = "$weatherDescriptionEmoji",
                            fontSize = 100.sp
                        )
                        Text(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                            text = "$weatherDescription",
                            fontSize = 20.sp


                        )
                        Button(
                            onClick = {
                                navController.navigate("forecastScreen")
                            },
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .align(Alignment.CenterHorizontally),
                        ) {
                            Text("Katso 7 päivän sääennuste")
                        }
                    }
                }
            }
        }
    }



