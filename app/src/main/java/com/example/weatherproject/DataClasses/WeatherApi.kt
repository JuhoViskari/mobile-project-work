package com.example.weatherproject.DataClasses


import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.io.IOException

/**
 * Interface for WeatherApi, include dataclasses and urls to response and fetch data
 * */
interface WeatherApi {
    /**
     * Fetch forecast data with url querys.
     *
     * @param latitude The latitude for location.
     * @param longitude The longitude for location.
     * @param current The current weather data.
     * @param hourly The hourly weather data.
     * @param daily The daily weather data.
     * @param timezone The timezone for UTF-8 time.
     * @return [ApiResponse] contain location, current, hourly daily and time data.
     */
    @GET("forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String,
        @Query("hourly") hourly: String,
        @Query("daily") daily: String,
        @Query("timezone") timezone: String
    ): ApiResponse
}

/**
 * Dataclass for response for weather api.
 * @property current The current data.
 * @property hourly The hourly data.
 * @property daily The daily data.
 */
data class ApiResponse(
    val current: CurrentData,
    val hourly: HourlyData,
    val daily: DailyData
)

/**
 * Dataclass for current data.
 * @property temperature_2m The current temperature.
 * @property weather_code The current weather code.
 */
data class CurrentData(
    val temperature_2m: Double,
    val weather_code: String
)

/**
 * Dataclass for hourly data
 * @property time The time for hourly data
 * @property weather_code The weather code for hourly data
 */
data class HourlyData(
    val time: List<String>,
    val weather_code: List<String>,
    val temperature_2m: List<Double>

)
/**
 * Dataclass for Daily data.
 * @property time The time for daily data.
 * @property temperature_2m_max The max temperature for daily data.
 * @property temperature_2m_min The min temperature for daily data.
 * @property weather_code The weather code for daily data.
 * @property sunrise The sunrise time for daily data.
 * @property sunset The sunset time for daily data.
 */
data class DailyData(
    val time: List<String>,
    val temperature_2m_max: List<Double>,
    val temperature_2m_min: List<Double>,
    val weather_code: List<String>,
    val sunrise: List<String>,
    val sunset: List<String>
)


// Singleton object for retrofit instance for Api.
object RetrofitInstance {
    private const val  BASE_URL = "https://api.open-meteo.com/v1/"

    private val getRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // Initialize weather api instance by lazy
    val weatherApi: WeatherApi by lazy {
        getRetrofit.create(WeatherApi::class.java)
    }

}

/**
 * Fetch weather data from api
 *
 * @param [latitude] The latitude location
 * @param [longitude] The longitude location
 * return [ApiResponse] contain weather data or null if get error
 */
suspend fun fetchWeatherData(latitude: Double, longitude: Double): ApiResponse? {
    return try {
     withContext(Dispatchers.IO) {
        RetrofitInstance.weatherApi.getWeather(
            latitude,
            longitude,
            "temperature_2m,weather_code",
            "temperature_2m,weather_code",
            "weather_code,temperature_2m_max,temperature_2m_min,sunrise,sunset",
            "auto")
    }
    } catch (e:IOException) {
        e.printStackTrace()
            null

    } catch (e:HttpException) {
        e.printStackTrace()
            null

        } catch (e:Exception) {
            e.printStackTrace()
            null
        }
    }