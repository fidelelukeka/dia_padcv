package id.fs.dia_padcv.ui.components.fields

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.data.local.entities.Distribution
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.utils.LocationState

@Composable
fun RequiredLocationField(
    viewModel: AppViewModel,
    distribution: Distribution, // ✅ distribution courante (persistée)
    locationState: LocationState,
    isLoading: Boolean,
    fetchLocation: () -> Unit
) {
    var isError by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // ✅ Dès que locationState est mis à jour, on enregistre dans distribution
    LaunchedEffect(locationState.latitude, locationState.longitude) {
        if (locationState.latitude.isNotEmpty() && locationState.longitude.isNotEmpty()) {
            viewModel.updateDistribution(
                distribution.copy(
                    latitude = locationState.latitude,
                    longitude = locationState.longitude,
                    altitude = locationState.altitude,
                    precision = locationState.precision
                )
            )
            isError = false
            Toast.makeText(context, "Coordonnées GPS enregistrées", Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Button(
            onClick = {
                fetchLocation()
                if (locationState.latitude.isEmpty() || locationState.longitude.isEmpty()) {
                    isError = true
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(if (isLoading) "Récupération..." else "Récupérer la localisation")
        }

        if (isError || distribution.latitude.isNullOrEmpty() || distribution.longitude.isNullOrEmpty()) {
            Text(
                text = "Le champ Localisation doit être rempli",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        // ✅ Affiche les coordonnées depuis distribution (persistées)
        if (!distribution.latitude.isNullOrEmpty() && !distribution.longitude.isNullOrEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Coordonnées GPS récupérées :", style = MaterialTheme.typography.titleMedium)
                    Text("Latitude : ${distribution.latitude}")
                    Text("Longitude : ${distribution.longitude}")
                    Text("Altitude : ${distribution.altitude ?: "N/A"} m")
                    Text("Précision : ${distribution.precision ?: "N/A"} m")
                }
            }
        }
    }
}
