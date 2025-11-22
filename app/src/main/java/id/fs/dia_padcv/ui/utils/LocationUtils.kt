package id.fs.dia_padcv.ui.utils

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

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
    val scope = rememberCoroutineScope()

    var state by remember {
        mutableStateOf(
            LocationState(
                hasPermission = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            )
        )
    }

    // Lanceur pour demander la permission si nécessaire
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        state = state.copy(hasPermission = granted)
        if (granted) {
            scope.launch {
                state = state.copy(isLoading = true)
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let {
                        state = state.copy(
                            latitude = it.latitude.toString(),
                            longitude = it.longitude.toString(),
                            altitude = it.altitude.toString(),
                            precision = it.accuracy.toString(),
                            isLoading = false
                        )
                    } ?: run {
                        state = state.copy(isLoading = false)
                    }
                }
            }
        }
    }

    // Fonction à appeler pour lancer la récupération
    val requestOrFetch = {
        if (state.hasPermission) {
            scope.launch {
                state = state.copy(isLoading = true)
                fusedLocationClient.lastLocation.addOnSuccessListener { loc ->
                    loc?.let {
                        state = state.copy(
                            latitude = it.latitude.toString(),
                            longitude = it.longitude.toString(),
                            altitude = it.altitude.toString(),
                            precision = it.accuracy.toString(),
                            isLoading = false
                        )
                    } ?: run {
                        state = state.copy(isLoading = false)
                    }
                }
            }
        } else {
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    return Triple(state, requestOrFetch, state.isLoading) as Triple<LocationState, () -> Unit, Boolean>
}
