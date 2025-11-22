package id.fs.dia_padcv.ui.components

import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
fun RequestPermissionsOnce(
    permissions: Array<String> = arrayOf(
        android.Manifest.permission.CAMERA,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ),
    onAllGranted: () -> Unit = {}
) {
    val context = LocalContext.current

    fun arePermissionsGranted(context: Context, permissions: Array<String>): Boolean {
        return permissions.all { ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        if (permissionsMap.all { it.value }) {
            onAllGranted()
        } else {
            Toast.makeText(
                context,
                "Veuillez accepter toutes les permissions pour continuer",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        if (arePermissionsGranted(context, permissions)) {
            // Déjà accordées
            onAllGranted()
        } else {
            // Sinon, demander les permissions
            permissionLauncher.launch(permissions)
        }
    }
}
