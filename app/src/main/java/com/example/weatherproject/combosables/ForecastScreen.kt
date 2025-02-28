package com.example.weatherproject.combosables

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weatherproject.DataClasses.ApiResponse
import com.example.weatherproject.DataClasses.fetchWeatherData
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt
/**
 * Showing daily basis forecast information and also hourly weather by time
 *
 * This Screen search forecast information by given location and show it to user
 * Using [latitude] and [longitude] parameters to specify locations
 *
 * @param navController which is used navigate from starting screen
 * @param latitude which used by forecast to specify latitude location
 * @param longitude which used by forecast to specify longitude location
 *
 * @author Juho Viskari
 */
@Composable
fun ForecastScreen(navController: NavController, latitude: Double, longitude: Double) {
    val coroutineScope = rememberCoroutineScope()

    var weatherResponse by remember {
        mutableStateOf<ApiResponse?>(null)
    }
    // make false to not showing hourlylist
    var showHourlyList by remember { mutableStateOf(false) }
    // make -1 to show  horlylist invidual date button
    var showselectedList by remember { mutableIntStateOf(-1) }

    // fetch weather data when composable is first launched
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val response = fetchWeatherData(latitude, longitude)
            weatherResponse = response
        }
    }


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Show loading indicator if weather data is not loaded yet
        if (weatherResponse == null) {
            CircularProgressIndicator()
            // else fetch from api
        } else {
            val days = weatherResponse?.daily?.time
            val dailyTemperaturemax = weatherResponse?.daily?.temperature_2m_max
            val dailyTemperaturemin = weatherResponse?.daily?.temperature_2m_min
            val dailyweathercode = weatherResponse?.daily?.weather_code
            val dailySunrise = weatherResponse?.daily?.sunrise
            val dailySunset = weatherResponse?.daily?.sunset

            // make sure that all data is avalaible with null point exeptions.
            if (days != null && dailyTemperaturemin != null && dailyTemperaturemax != null
                && dailyweathercode != null
            ) {
                LazyColumn {
                    items(days.size) { index ->
                        // weekday and datetime
                        val time = days[index]
                        val date = LocalDate.parse(time)
                        val formattedtime = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                        val weekDay = date.dayOfWeek

                        // min daily temperature
                        val mintemp = dailyTemperaturemin[index]
                        val roundedmintemp = mintemp.roundToInt()

                        // max daily temperature
                        val maxtemp = dailyTemperaturemax[index]
                        val roundedmaxtemp = maxtemp.roundToInt()
                        // weathercode
                        val wc = dailyweathercode[index]

                        // daily sunrise time
                        val dailyrise = dailySunrise?.get(index)
                        val timedailyrise = dailyrise?.let { LocalDateTime.parse(it) }
                        val formattedtimerise =
                            timedailyrise?.format(DateTimeFormatter.ofPattern("HH:mm"))

                        // daily sunset time
                        val dailyset = dailySunset?.get(index)
                        val timedailyset = dailyset?.let { LocalDateTime.parse(it) }
                        val formattedtimeset =
                            timedailyset?.format(DateTimeFormatter.ofPattern("HH:mm"))

                        // switch case to change default english dates to finnish dates.
                        val finnishWeekDay = when (weekDay.name) {
                            "MONDAY" -> "MAANANTAI"
                            "TUESDAY" -> "TIISTAI"
                            "WEDNESDAY" -> "KESKIVIIKKO"
                            "THURSDAY" -> "TORSTAI"
                            "FRIDAY" -> "PERJANTAI"
                            "SATURDAY" -> "LAUANTAI"
                            "SUNDAY" -> "SUNNUNTAI"
                            else -> "PROBLEMS WITH WEEKDAYS"

                        }

                        // switch case to map weathercodes to emoji
                        val weatherDescriptionEmoji = when (wc.toIntOrNull()) {
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

                        // fetch hourly data
                        val hours = weatherResponse?.hourly?.time
                        val hourlyweathercode = weatherResponse?.hourly?.weather_code
                        val hourlytemperature = weatherResponse?.hourly?.temperature_2m


                        // make sure that fetching hours is not null and make parsing
                        // and map filter by day
                        if (hours != null) {
                            val filteredHours =
                                hours.withIndex().filter { it.value.startsWith(time) }
                                    .map { it.index }


                            // UI for button
                            Box {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    // toggle hourly list visibility on button click
                                    Button(
                                        onClick = {
                                            showselectedList = index
                                            showHourlyList = !showHourlyList
                                        },

                                        modifier = Modifier.padding(top = 16.dp),
                                        shape = RectangleShape,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)

                                    ) {
                                        // UI for showing daily forecast
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {

                                            Spacer(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(1.dp)
                                                    .background(Color.Gray)
                                                    .padding(top = 8.dp)
                                            )
                                            Text(
                                                text = "$weatherDescriptionEmoji",
                                                fontSize = 70.sp,
                                                modifier = Modifier.padding(bottom = 16.dp)
                                            )
                                            Text(
                                                text = "$finnishWeekDay",
                                                fontSize = 20.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Text(
                                                text = "$formattedtime",
                                                fontSize = 20.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Text(
                                                text = "$roundedmintemp / $roundedmaxtemp °C",
                                                fontSize = 20.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Text(
                                                text = "\uD83C\uDF05 $formattedtimerise",
                                                fontSize = 20.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                            Text(
                                                text = "\uD83C\uDF07 $formattedtimeset",
                                                fontSize = 20.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(bottom = 8.dp)
                                            )
                                        }
                                    }
                                    // if hourlist is visible and the selected list match current
                                    // index display hourly values
                                    if (showHourlyList && showselectedList == index) {
                                        Row(
                                            // make hourly list to horizontal scrolling style
                                            modifier = Modifier.horizontalScroll(
                                                rememberScrollState()
                                            )
                                        ) {
                                            filteredHours.forEach { index ->
                                                val timehourly = hours[index]
                                                val formatTime =
                                                    LocalDateTime.parse(
                                                        timehourly,
                                                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                                                    )
                                                val formattedtimehourly =
                                                    formatTime.format(DateTimeFormatter.ofPattern("HH:mm"))
                                                val hourTemp = hourlytemperature?.get(index)
                                                val roundedTemperaturehourly = hourTemp?.roundToInt()

                                                // switch case to show hourly emoji
                                                val weatherDescriptionEmojihourly =
                                                    when (hourlyweathercode?.get(index)
                                                        ?.toIntOrNull()) {
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
                                                // UI for hourly scroll
                                                Column(
                                                    modifier = Modifier
                                                        .padding(8.dp)

                                                ) {

                                                    Box(
                                                        modifier = Modifier
                                                            .padding(top = 30.dp)
                                                    ) {
                                                        Text(
                                                            text = formattedtimehourly,
                                                            modifier = Modifier.padding(4.dp),
                                                            fontSize = 15.sp,
                                                            color = Color.White
                                                        )
                                                        Box(
                                                            modifier = Modifier
                                                                .padding(top = 30.dp)
                                                        ) {

                                                            Text(
                                                                text = weatherDescriptionEmojihourly,
                                                                fontSize = 25.sp,
                                                                color = Color.White
                                                            )
                                                        Box(
                                                            modifier = Modifier
                                                                .padding(top = 30.dp)
                                                        ) {
                                                            Text(
                                                                text = "$roundedTemperaturehourly °C",
                                                                modifier = Modifier.padding(2.dp),
                                                                fontSize = 15.sp,
                                                                color = Color.White,
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
                        }
                    }
                }
            }
        }
    }
}



