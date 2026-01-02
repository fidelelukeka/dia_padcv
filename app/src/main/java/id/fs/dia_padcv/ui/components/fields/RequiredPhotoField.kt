package id.fs.dia_padcv.ui.components.fields

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun RequiredPhotoField(
    imageUri: Uri?, // ✅ une seule image
    cameraLauncher: ManagedActivityResultLauncher<Void?, Bitmap?>,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    onClear: () -> Unit
) {
    Text("Photo du bénéficiaire", style = MaterialTheme.typography.titleMedium)

    if (imageUri == null) {
        Text(
            text = "Le champ Photo doit être rempli",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = { cameraLauncher.launch(null) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text("Prendre une photo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { galleryLauncher.launch("image/*") },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            )
        ) {
            Text("Importer une photo depuis la galerie")
        }
    } else {
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onClear() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Supprimer la photo")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Décodage direct du fichier
        val bitmap = remember(imageUri) {
            try {
                val file = File(imageUri.path ?: "")
                BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
            } catch (e: Exception) {
                null
            }
        }

        bitmap?.let {
            Image(
                bitmap = it,
                contentDescription = "Photo bénéficiaire",
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .fillMaxHeight(0.5f)
                    .clip(RoundedCornerShape(16.dp)),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop
            )
        }
    }
}