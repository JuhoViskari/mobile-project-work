package com.example.weatherproject.Locationfiles

import android.app.Application
import android.location.Location
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import com.example.weatherproject.Locationfiles.LocationRepository

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    private val locationRepository = LocationRepository(application)
    private val _location = mutableStateOf<Location?>(null)
    val location: androidx.compose.runtime.State<Location?> = _location

    fun startLocationUpdates() {
        locationRepository.startLocationUpdates { location ->
            _location.value = location
        }
    }
}