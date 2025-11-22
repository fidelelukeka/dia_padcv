package id.fs.dia_padcv.ui.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import java.io.File
import java.io.FileOutputStream

/**
 * Sauvegarde un Bitmap (pris depuis la caméra) dans le stockage local de l'app
 * et retourne un Uri pointant vers le fichier local.
 * @param prefix Préfixe du nom de fichier (optionnel, défaut "photo")
 */
fun saveBitmapToLocal(context: Context, bitmap: Bitmap, prefix: String = "photo"): Uri {
    val directory = File(context.filesDir, "entrepots").apply { if (!exists()) mkdirs() }

    val file = File(directory, "${prefix}_${System.currentTimeMillis()}.jpg")

    return try {
        FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out) }
        Log.d("ImageUtils", "✅ Image caméra sauvegardée localement : ${file.absolutePath}")
        file.toUri()
    } catch (e: Exception) {
        Log.e("ImageUtils", "❌ Erreur lors de la sauvegarde du bitmap", e)
        file.toUri()
    }
}

/**
 * Copie une image choisie dans la galerie vers le stockage local de l'app
 * et retourne un Uri vers le fichier copié.
 */
fun saveGalleryImageToLocal(context: Context, uri: Uri): Uri {
    val inputStream = context.contentResolver.openInputStream(uri) ?: return uri
    val directory = File(context.filesDir, "entrepots").apply { if (!exists()) mkdirs() }

    val file = File(directory, "gallery_${System.currentTimeMillis()}.jpg")

    return try {
        FileOutputStream(file).use { out -> inputStream.copyTo(out) }
        Log.d("ImageUtils", "✅ Image galerie sauvegardée localement : ${file.absolutePath}")
        file.toUri()
    } catch (e: Exception) {
        Log.e("ImageUtils", "❌ Erreur lors de la copie d'image de la galerie", e)
        uri
    } finally {
        inputStream.close()
    }
}

/**
 * Composable utilitaire à placer dans l'écran pour demander automatiquement
 * les permissions caméra et lecture du stockage au démarrage.
 */
@Composable
fun RequestPermissions() {
    val context = LocalContext.current
    val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        results.forEach { (perm, granted) ->
            Log.d("Permissions", "🔹 $perm : ${if (granted) "accordée" else "refusée"}")
        }
    }

    LaunchedEffect(Unit) {
        val notGranted = permissions.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        if (notGranted.isNotEmpty()) {
            Log.d("Permissions", "⏳ Demande automatique de permissions...")
            launcher.launch(notGranted.toTypedArray())
        } else {
            Log.d("Permissions", "✅ Toutes les permissions déjà accordées.")
        }
    }
}