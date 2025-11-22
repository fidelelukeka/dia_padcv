package id.fs.dia_padcv.ui
//
//import android.app.Activity
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import com.google.zxing.integration.android.IntentIntegrator
//
//// Helper pour obtenir l'Activity depuis LocalContext
//@Composable
//fun localActivity(): Activity? {
//    val context = LocalContext.current
//    return when (context) {
//        is Activity -> context
//        else -> null
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun DistributionScreenOld(function: () -> Boolean) {
//    // État pour stocker le contenu du QR code
//    var qrCodeContent by remember { mutableStateOf("") }
//
//    val qrLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            val contents = result.data?.getStringExtra("SCAN_RESULT")
//            qrCodeContent = contents ?: ""
//        }
//    }
//
//    val activity = LocalActivity()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        verticalArrangement = Arrangement.spacedBy(12.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text("Scanner un QR Code", style = MaterialTheme.typography.titleLarge)
//
//        Button(
//            onClick = {
//                activity?.let {
//                    val intent = IntentIntegrator(it).apply {
//                        setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
//                        setPrompt("Scannez un QR Code")
//                        setBeepEnabled(true)
//                        setOrientationLocked(false)
//                    }.createScanIntent()
//                    qrLauncher.launch(intent)
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text("📷 Scanner un QR Code")
//        }
//
//        if (qrCodeContent.isNotEmpty()) {
//            OutlinedTextField(
//                value = qrCodeContent,
//                onValueChange = { qrCodeContent = it },
//                label = { Text("Résultat du QR Code") },
//                modifier = Modifier.fillMaxWidth(),
//                readOnly = true
//            )
//        }
//    }
//}