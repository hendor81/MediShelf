package it.hendorsoftware.medishelf.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme

/** Bersagli della guida, nello stesso ordine dei cinque tooltip. */
enum class TutorialTarget(val title: Int, val body: Int) {
    Summary(R.string.tutorial_summary_title, R.string.tutorial_summary_body),
    Search(R.string.tutorial_search_title, R.string.tutorial_search_body),
    Medicines(R.string.tutorial_medicines_title, R.string.tutorial_medicines_body),
    Add(R.string.tutorial_add_title, R.string.tutorial_add_body),
    Navigation(R.string.tutorial_navigation_title, R.string.tutorial_navigation_body),
}

/** Coordinate nello stesso root Compose dello scaffold, barra inferiore inclusa. */
val LocalTutorialTargets = compositionLocalOf<MutableMap<TutorialTarget, Rect>?> { null }
/** Passo corrente: null rende i bersagli passivi durante il normale utilizzo. */
val LocalTutorialStep = compositionLocalOf<Int?> { null }

/** Registra il bersaglio e lo porta nella porzione visibile prima di spiegarlo. */
fun Modifier.tutorialTarget(target: TutorialTarget): Modifier = composed {
    val targets = LocalTutorialTargets.current
    val step = LocalTutorialStep.current
    val requester = remember { BringIntoViewRequester() }
    LaunchedEffect(step) {
        if (step == target.ordinal) requester.bringIntoView()
    }
    DisposableEffect(targets, target) {
        onDispose { targets?.remove(target) }
    }
    this.bringIntoViewRequester(requester).onGloballyPositioned {
        targets?.set(target, it.boundsInRoot())
    }
}

/**
 * Guida modale leggera: evidenzia un elemento reale e intercetta i tap sottostanti.
 * BACK e Salta chiudono; Avanti/Fine avanzano senza attivare il bersaglio.
 * Le coordinate sono aggiornate anche dopo scroll e rotazione.
 */
@Composable
fun HomeTutorialOverlay(
    step: Int,
    targets: Map<TutorialTarget, Rect>,
    onNext: () -> Unit,
    onDismiss: () -> Unit,
) {
    val target = TutorialTarget.entries[step]
    val title = stringResource(target.title)
    val scrim = MaterialTheme.colorScheme.scrim.copy(alpha = TutorialTokens.ScrimAlpha)
    BackHandler(onBack = onDismiss)
    BoxWithConstraints(
        Modifier.fillMaxSize()
            .clickable(remember { MutableInteractionSource() }, indication = null) { }
            .semantics { paneTitle = title },
    ) {
        val maxMessageHeight = maxHeight * TutorialTokens.MaxHeightFraction
        val bounds = targets[target]
        val animatedBounds by animateRectAsState(
            targetValue = bounds ?: Rect.Zero,
            animationSpec = tween(TutorialTokens.TransitionMillis),
            label = "tutorialSpotlight",
        )
        val cornerRadius = with(androidx.compose.ui.platform.LocalDensity.current) {
            MediShelfDimens.CornerMedium.toPx()
        }
        Canvas(Modifier.fillMaxSize()) {
            val path = Path().apply {
                fillType = PathFillType.EvenOdd
                addRect(Rect(0f, 0f, size.width, size.height))
                if (bounds != null) {
                    addRoundRect(RoundRect(animatedBounds, CornerRadius(cornerRadius)))
                }
            }
            drawPath(path, scrim)
        }
        // I controlli inferiori sono spiegati dall’alto, gli altri dal basso.
        // Il contenuto scorre per mantenere disponibili le azioni con font grandi.
        Crossfade(
            targetState = step,
            modifier = Modifier.fillMaxSize(),
            animationSpec = tween(TutorialTokens.TransitionMillis),
            label = "tutorialMessage",
        ) { displayedStep ->
            val displayedTarget = TutorialTarget.entries[displayedStep]
            Box(Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier.align(if (displayedStep >= TutorialTarget.Add.ordinal) Alignment.TopCenter else Alignment.BottomCenter)
                        .safeDrawingPadding().padding(MediShelfDimens.ScreenPadding)
                        .widthIn(max = TutorialTokens.MaxWidth)
                        .heightIn(max = maxMessageHeight),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shadowElevation = TutorialTokens.MessageElevation,
                    border = BorderStroke(TutorialTokens.MessageBorderWidth, MaterialTheme.colorScheme.primary),
                ) {
                    Column(
                        Modifier.verticalScroll(rememberScrollState()).padding(MediShelfDimens.SpacingMedium),
                        verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmallMedium),
                    ) {
                        Text(stringResource(R.string.tutorial_progress, displayedStep + 1, TutorialTarget.entries.size),
                            style = MaterialTheme.typography.labelLarge)
                        Text(stringResource(displayedTarget.title), style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                        Text(stringResource(displayedTarget.body), style = MaterialTheme.typography.bodyMedium)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = onDismiss) { Text(stringResource(R.string.onboarding_skip)) }
                            Button(onClick = onNext) {
                                Text(stringResource(if (displayedStep == TutorialTarget.entries.lastIndex) R.string.tutorial_finish else R.string.onboarding_start))
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Token visuali locali della guida. */
private object TutorialTokens {
    const val TransitionMillis = 240
    val MessageElevation = 12.dp
    val MessageBorderWidth = 1.dp
    const val ScrimAlpha = 0.72f
    const val MaxHeightFraction = 0.5f
    val MaxWidth = androidx.compose.ui.unit.Dp(480f)
}

/** Preview del tooltip iniziale, indipendente dal grafo e dai dati. */
@Preview(showBackground = true)
@Preview(showBackground = true, fontScale = 1.5f, widthDp = 320)
@Composable
private fun HomeTutorialPreview() {
    MediShelfTheme { HomeTutorialOverlay(0, emptyMap(), {}, {}) }
}
