import android.content.res.Configuration
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.fs.dia_padcv.ui.*
import id.fs.dia_padcv.ui.beneficiary.BeneficiaryScreen
import id.fs.dia_padcv.ui.distribution.DistributionScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    NavHost(navController = navController, startDestination = "login") {

        // -------- LOGIN --------
        composable("login") {
            LoginScreen(viewModel = appViewModel) {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        // -------- SYNC --------
        composable("sync") {
            SyncScreen(viewModel = appViewModel) {
                navController.navigate("home") {
                    popUpTo("sync") { inclusive = true }
                }
            }
        }

        // -------- HOME --------
        composable("home") {
            val configuration = LocalConfiguration.current
            val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            val columns = if (isLandscape) 4 else 2

            val buttonColors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50), // Vert vif
                contentColor = Color.White
            )
            val buttonShape = RoundedCornerShape(8.dp) // Coins modérés

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(columns) }) {
                    Text(
                        "Bienvenue ! Choisissez une option",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                item {
                    Button(
                        onClick = { navController.navigate("beneficiaire") },
                        modifier = Modifier.fillMaxWidth().height(80.dp),
                        colors = buttonColors,
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
                        colors = buttonColors,
                        shape = buttonShape
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Distributions")
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
                        colors = buttonColors,
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
                        colors = buttonColors,
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
                        colors = buttonColors,
                        shape = buttonShape
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = "Synchroniser")
                        Spacer(Modifier.width(8.dp))
                        Text("Synchroniser")
                    }
                }
            }
        }

        // -------- BENEFICIAIRE --------
        composable("beneficiaire") {
            BeneficiaryScreen(viewModel = appViewModel) {
                navController.popBackStack()
            }
        }

        // -------- DISTRIBUTIONS --------
        composable("distributions") {
            DistributionScreen(viewModel = appViewModel) {
                navController.popBackStack()
            }
        }

        // -------- ENTREPOT --------
        composable("entrepot") {
            EntrepotScreen(viewModel = appViewModel) {
                navController.popBackStack()
            }
        }

        // -------- PACKAGE --------
        composable("package") {
            PackageScreen(viewModel = appViewModel) {
                navController.popBackStack()
            }
        }
    }
}
