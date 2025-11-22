import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                navController.navigate("distributions") {
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Bienvenue ! Choisissez une option", style = MaterialTheme.typography.titleLarge)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("beneficiaire") },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Gérer les bénéficiaires") }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("distributions") },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Gérer les distributions") }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        Log.d("Navigation", "Bouton Entrepôt cliqué")
                        navController.navigate("entrepot")
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Enregistrer un entrepôt") }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("package") },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Enregistrer un colis") }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("sync") },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) { Text("Synchroniser") }
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
