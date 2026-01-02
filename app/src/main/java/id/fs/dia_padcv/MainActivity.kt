package id.fs.dia_padcv

import AppNavHost
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import id.fs.dia_padcv.data.local.database.AppDatabase
import id.fs.dia_padcv.data.repository.AppRepository
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.theme.DIA_PADCVTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repo = AppRepository(db.appDao())

        setContent {
            // ✅ Création du ViewModel avec factory
            val appViewModel: AppViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        @Suppress("UNCHECKED_CAST")
                        return AppViewModel(repo) as T
                    }
                }
            )

            val navController = rememberNavController()

            // ✅ Appliquer ton thème ici
            DIA_PADCVTheme {
                AppNavHost(
                    navController = navController,
                    appViewModel = appViewModel
                )
            }
        }
    }
}
