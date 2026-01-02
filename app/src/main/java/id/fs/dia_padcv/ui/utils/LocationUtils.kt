package id.fs.dia_padcv.ui.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices

data class LocationState(
    val latitude: String = "",
    val longitude: String = "",
    val altitude: String = "",
    val precision: String = "",
    val hasPermission: Boolean = false,
    val isLoading: Boolean = false
)

/**
 * Hook Compose réutilisable pour gérer la localisation (latitude, longitude, altitude, précision).
 */
@Composable
fun useLocation(): Triple<LocationState, () -> Unit, Boolean> {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var state by remember {
        mutableStateOf(
            LocationState(
                hasPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    // Fonction factorisée pour récupérer la localisation
    fun fetchLocation() {
        state = state.copy(isLoading = true)
        fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
            state = if (loc != null) {
                state.copy(
                    latitude = loc.latitude.toString(),
                    longitude = loc.longitude.toString(),
                    altitude = loc.altitude.toString(),
                    precision = loc.accuracy.toString(),
                    isLoading = false
                )
            } else {
                state.copy(isLoading = false)
            }
        }
    }

    // Lanceur pour demander la permission si nécessaire
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        state = state.copy(hasPermission = granted)
        if (granted) fetchLocation()
    }

    // Fonction à appeler pour lancer la récupération
    val requestOrFetch = {
        if (state.hasPermission) {
            fetchLocation()
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    return Triple(state, requestOrFetch, state.isLoading)
}
