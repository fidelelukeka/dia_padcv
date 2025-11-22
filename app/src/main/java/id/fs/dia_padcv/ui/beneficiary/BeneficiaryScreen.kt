package id.fs.dia_padcv.ui.beneficiary

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.StepNavigationBar
import id.fs.dia_padcv.ui.beneficiary.steps.StepAutresInformations
import id.fs.dia_padcv.ui.beneficiary.steps.StepAgriculture
import id.fs.dia_padcv.ui.beneficiary.steps.StepAlimentation
import id.fs.dia_padcv.ui.beneficiary.steps.StepBiensMateriels
import id.fs.dia_padcv.ui.beneficiary.steps.StepCommentairesEtScores
import id.fs.dia_padcv.ui.beneficiary.steps.StepHabitation
import id.fs.dia_padcv.ui.beneficiary.steps.StepIdentite
import id.fs.dia_padcv.ui.beneficiary.steps.StepMenage
import id.fs.dia_padcv.ui.beneficiary.steps.StepResume
import id.fs.dia_padcv.ui.beneficiary.steps.StepWASH


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeneficiaryScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    val animatedProgress by animateFloatAsState(
        targetValue = currentStep / 10f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Formulaire bénéficiaire") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        bottomBar = {
            StepNavigationBar(
                currentStep = currentStep,
                totalSteps = 10,
                onPrevious = { if (currentStep > 1) currentStep-- },
                onNext = { if (currentStep < 10) currentStep++ }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                )
            }
            item {
            Spacer(Modifier.height(16.dp))
            }
            item {
                when (currentStep) {
                    1 -> StepIdentite(viewModel)
                    2 -> StepMenage(viewModel)
                    3 -> StepHabitation(viewModel)
                    4 -> StepWASH(viewModel)
                    5 -> StepAgriculture(viewModel)
                    6 -> StepBiensMateriels(viewModel)
                    7 -> StepAlimentation(viewModel)
                    8 -> StepCommentairesEtScores(viewModel)
                    9 -> StepAutresInformations(viewModel)
                    10 -> StepResume(viewModel)
                }
            }
        }
    }
}