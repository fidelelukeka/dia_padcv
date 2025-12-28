package id.fs.dia_padcv.ui

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import id.fs.dia_padcv.ui.components.AppScaffold

@Composable
fun HomeScreen(
    navController: NavHostController,
) {
    val buttonShape = RoundedCornerShape(8.dp)

    AppScaffold(
        title = "Accueil"
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2), // ✅ toujours 2 colonnes en portrait
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(2) }) {
                Text(
                    "Bienvenue ! Choisissez une option",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            // ✅ Utilisation des couleurs M3 baseline
            item {
                Button(
                    onClick = { navController.navigate("beneficiaire") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = buttonShape
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Bénéficiaires")
                    Spacer(Modifier.width(8.dp))
                    Text("Gérer les bénéficiaires")
                }
            }

            item {
                Button(
                    onClick = { navController.navigate("distributions") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    shape = buttonShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Distributions")
                    Spacer(Modifier.width(8.dp))
                    Text("Gérer les distributions")
                }
            }

            item {
                Button(
                    onClick = {
                        Log.d("Navigation", "Bouton Entrepôt cliqué")
                        navController.navigate("entrepot")
                    },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    ),
                    shape = buttonShape
                ) {
                    Icon(Icons.Default.Home, contentDescription = "Entrepôt")
                    Spacer(Modifier.width(8.dp))
                    Text("Enregistrer un entrepôt")
                }
            }

            item {
                Button(
                    onClick = { navController.navigate("package") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    ),
                    shape = buttonShape
                ) {
                    Icon(Icons.Default.Inventory, contentDescription = "Colis")
                    Spacer(Modifier.width(8.dp))
                    Text("Enregistrer un colis")
                }
            }

            item {
                Button(
                    onClick = { navController.navigate("sync") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = buttonShape
                ) {
                    Icon(Icons.Default.Sync, contentDescription = "Synchroniser")
                    Spacer(Modifier.width(8.dp))
                    Text("Synchroniser")
                }
            }

            item {
                Button(
                    onClick = { navController.navigate("profile") },
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = buttonShape
                ) {
                    Icon(Icons.Default.Person, contentDescription = "Profil")
                    Spacer(Modifier.width(8.dp))
                    Text("Mon Profil")
                }
            }
        }
    }
}
