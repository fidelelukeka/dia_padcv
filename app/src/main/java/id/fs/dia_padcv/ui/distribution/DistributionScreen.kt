package id.fs.dia_padcv.ui.distribution

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import id.fs.dia_padcv.ui.AppViewModel
import id.fs.dia_padcv.ui.components.AppScaffold
import id.fs.dia_padcv.ui.components.StepNavigationBar
import id.fs.dia_padcv.ui.distribution.steps.StepCommentairesSuggestion
import id.fs.dia_padcv.ui.distribution.steps.StepDistributionEngrais
import id.fs.dia_padcv.ui.distribution.steps.StepDistributionSemence
import id.fs.dia_padcv.ui.distribution.steps.StepIdentite
import id.fs.dia_padcv.ui.distribution.steps.StepInformationGeographique
import id.fs.dia_padcv.ui.distribution.steps.StepResume


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributionScreen(
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }
    val uiMessage by viewModel.uiMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val animatedProgress by animateFloatAsState(
        targetValue = currentStep / 6f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(uiMessage) {
        uiMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearUiMessage()
        }
    }

    AppScaffold(
        title = "Formulaire bénéficiaire",
        onBack = onBack,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = {
            StepNavigationBar(
                currentStep = currentStep,
                totalSteps = 6,
                onPrevious = { if (currentStep > 1) currentStep-- },
                onNext = { if (currentStep < 6) currentStep++ }
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
                    1 -> StepInformationGeographique(viewModel)
                    2 -> StepIdentite(viewModel)
                    3 -> StepDistributionSemence(viewModel)
                    4 -> StepDistributionEngrais(viewModel)
                    5 -> StepCommentairesSuggestion(viewModel)
                    6 -> StepResume(viewModel)
                }
            }
        }
    }
}