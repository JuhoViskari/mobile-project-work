package com.example.weatherproject.combosables

import android.Manifest
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weatherproject.Locationfiles.LocationViewModel
import java.util.Locale
/**
 * Composable function search user's current location
 *
 * Requests users location permissions, start location updates,
 * and displays the human-readable address of the current location.
 *
 * @param selectLocation A callback function to invoke latitude and longitude
 */
@Composable
fun LocationSearcher(selectLocation: @Composable (latitude: Double, longitude: Double) -> Unit) {
    val viewModel: LocationViewModel = viewModel()
    val location = viewModel.location

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            // Check if all requested permissions have been granted
            val allPermissionsGranted = permissions.entries.all { it.value }
            if (allPermissionsGranted) {
                viewModel.startLocationUpdates()
            }
        }
    )

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // show location

    // Fetch human-readable address based on location
    location.value?.let { loc ->
        val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
        var city: String? = null
        var country: String? = null
        try {
            val addresses = geocoder.getFromLocation(loc.latitude, loc.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    city = address.locality
                    country = address.countryName
                }
            }
        } catch (e: Exception) {
            Text(
                text = "Oletko avaruudessa, lokaatiota ei löytynyt"
            )
        }

        Text(
            text = location.value?.let { loc ->
                "Sijainti: $city, $country"
            } ?: "",
            color = Color.White
        )

        if (location.value != null) {
            selectLocation(loc.latitude, loc.longitude)
        } else {
            Text(
                text = "sijainti ei käytettävissä"
            )
        }

    }
}