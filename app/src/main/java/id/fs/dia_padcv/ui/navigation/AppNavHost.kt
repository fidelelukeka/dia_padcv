import android.content.Context
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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.fs.dia_padcv.ui.*
import id.fs.dia_padcv.ui.beneficiary.BeneficiaryScreen
import id.fs.dia_padcv.ui.components.AppScaffold
import id.fs.dia_padcv.ui.distribution.DistributionScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    appViewModel: AppViewModel
) {
    val context = LocalContext.current
    val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val isFirstLaunch = prefs.getBoolean("isFirstLaunch", true)

    NavHost(
        navController = navController,
        startDestination = if (isFirstLaunch) "permissions" else "login"
    ) {

        composable("permissions") {
            PermissionScreen {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }
        // -------- LOGIN --------
        composable("login") {
            LoginScreen(viewModel = appViewModel) {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            }
        }

        // -------- SYNC --------
        composable("sync") {
            SyncScreen(
                viewModel = appViewModel,
                onBack = { navController.popBackStack() },
            )
        }

        // -------- HOME --------
        composable("home") {
            HomeScreen(
                navController = navController,
                viewModel = appViewModel
            )
        }

        // -------- BENEFICIAIRE --------
        composable("beneficiaire") {
            BeneficiaryScreen(viewModel = appViewModel) {
                navController.popBackStack()
            }
        }

        // -------- DISTRIBUTIONS --------
        composable("distributions") {
            DistributionScreen(
                viewModel = appViewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    // ✅ Redirection après succès
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
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

        // -------- PROFILE --------
        composable("profile") {
            ProfileScreen(viewModel = appViewModel) {
                navController.popBackStack()
            }
        }
    }
}
