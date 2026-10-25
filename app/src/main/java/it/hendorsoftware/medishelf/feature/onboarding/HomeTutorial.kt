package it.hendorsoftware.medishelf.feature.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
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

/** Ingombro del messaggio nel root; la lista riserva questo spazio prima di scorrere. */
data class TutorialMessageArea(val bounds: Rect, val atTop: Boolean)

/** Collegamento locale fra il messaggio modale e il viewport della Home. */
val LocalTutorialMessageArea = compositionLocalOf<MutableState<TutorialMessageArea?>?> { null }

/** Geometria del viewport prima della riserva per il messaggio. */
val LocalTutorialViewportBounds = compositionLocalOf<MutableState<Rect?>?> { null }

/**
 * Riduce il viewport scorrevole in corrispondenza del messaggio: bringIntoView
 * tiene così conto dell’overlay, senza modificare il layout fuori dalla guida.
 */
fun Modifier.tutorialViewport(): Modifier = composed {
    val area = LocalTutorialMessageArea.current?.value
    val viewportBounds = LocalTutorialViewportBounds.current
    var viewport by remember { mutableStateOf(Rect.Zero) }
    val density = LocalDensity.current
    val gap = with(density) { MediShelfDimens.SpacingSmall.toPx() }
    val top = if (area?.atTop == true) (area.bounds.bottom + gap - viewport.top).coerceAtLeast(0f) else 0f
    val bottom = if (area?.atTop == false) (viewport.bottom - area.bounds.top + gap).coerceAtLeast(0f) else 0f
    this.onGloballyPositioned {
        viewport = it.boundsInRoot()
        viewportBounds?.value = viewport
    }
        .padding(top = with(density) { top.toDp() }, bottom = with(density) { bottom.toDp() })
}

/** Registra il bersaglio e lo porta nella porzione visibile prima di spiegarlo. */
fun Modifier.tutorialTarget(target: TutorialTarget): Modifier = composed {
    val targets = LocalTutorialTargets.current
    val step = LocalTutorialStep.current
    val requester = remember { BringIntoViewRequester() }
    val messageArea = LocalTutorialMessageArea.current?.value
    LaunchedEffect(step, messageArea) {
        // Attende il nuovo viewport prima di allineare il bersaglio.
        withFrameNanos { }
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
    val messageArea = LocalTutorialMessageArea.current
    DisposableEffect(Unit) { onDispose { messageArea?.value = null } }
    // Conserva dimensione e origine del root anche in finestre ridimensionate.
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val overlayWidth = maxWidth
        val overlayHeight = maxHeight
        val bounds = targets[target]
        // La maschera usa lo stesso root dei bersagli: il Popup può avere inset
        // e dimensioni diversi, soprattutto nell'area della navigazione di sistema.
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
        // Una finestra focusable isola tastiera e accessibilità dalla Home sottostante.
        Popup(
            popupPositionProvider = TutorialPopupPosition,
            onDismissRequest = onDismiss,
            properties = PopupProperties(focusable = true, clippingEnabled = false),
        ) {
            BackHandler(onBack = onDismiss)
            BoxWithConstraints(
                Modifier.size(overlayWidth, overlayHeight)
                    .pointerInput(Unit) { detectTapGestures { } }
                    .semantics { paneTitle = title },
            ) {
                val density = LocalDensity.current
                val viewportBounds = LocalTutorialViewportBounds.current?.value
                val viewportBudget = viewportBounds?.let {
                    with(density) { it.height.toDp() } - MediShelfDimens.MinInteractiveSize -
                        MediShelfDimens.ScreenPadding * 2
                } ?: maxHeight
                val maxMessageHeight = minOf(
                    maxHeight * TutorialTokens.MaxHeightFraction,
                    viewportBudget.coerceAtLeast(MediShelfDimens.MinInteractiveSize),
                )
                val heightPx = with(LocalDensity.current) { maxHeight.toPx() }
                // La scelta usa la geometria effettiva e resta stabile durante lo scroll:
                // ricalcolarla sul bersaglio già spostato farebbe oscillare il messaggio.
                var atTop by remember(step, maxWidth, maxHeight) { mutableStateOf<Boolean?>(null) }
                LaunchedEffect(bounds, heightPx) {
                    if (atTop == null && bounds != null && bounds.height > 0f) {
                        atTop = bounds.top > heightPx - bounds.bottom
                    }
                }
                // Il messaggio occupa il lato con più spazio rispetto al bersaglio;
                // il viewport della Home viene ridotto prima di riportarlo in vista.
                Crossfade(
                    targetState = step,
                    modifier = Modifier.fillMaxSize(),
                    animationSpec = tween(TutorialTokens.TransitionMillis),
                    label = "tutorialMessage",
                ) { displayedStep ->
                    val displayedTarget = TutorialTarget.entries[displayedStep]
                    val messageFocus = remember { FocusRequester() }
                    val explanationScroll = rememberScrollState()
                    LaunchedEffect(displayedStep) {
                        if (displayedStep == step) {
                            explanationScroll.scrollTo(0)
                            // Il focus resta fuori dal contenuto scorrevole: non deve
                            // portare Avanti in vista nascondendo la nuova spiegazione.
                            messageFocus.requestFocus()
                        }
                    }
                    Box(Modifier.fillMaxSize()) {
                        Surface(
                            modifier = Modifier.align(if (atTop == true) Alignment.TopCenter else Alignment.BottomCenter)
                                .safeDrawingPadding().padding(MediShelfDimens.ScreenPadding)
                                .widthIn(max = TutorialTokens.MaxWidth)
                                .heightIn(max = maxMessageHeight)
                                .testTag(HomeTutorialTestTags.Message)
                                .focusRequester(messageFocus)
                                .focusable(enabled = displayedStep == step)
                                .onGloballyPositioned {
                                    if (displayedStep == step) {
                                        messageArea?.value = TutorialMessageArea(it.boundsInRoot(), atTop == true)
                                    }
                                },
                            shape = MaterialTheme.shapes.large,
                            color = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            shadowElevation = TutorialTokens.MessageElevation,
                            border = BorderStroke(TutorialTokens.MessageBorderWidth, MaterialTheme.colorScheme.primary),
                        ) {
                            Column(
                                Modifier.verticalScroll(explanationScroll).padding(MediShelfDimens.SpacingMedium),
                                verticalArrangement = Arrangement.spacedBy(MediShelfDimens.SpacingSmallMedium),
                            ) {
                                Text(stringResource(R.string.tutorial_progress, displayedStep + 1, TutorialTarget.entries.size),
                                    style = MaterialTheme.typography.labelLarge)
                                Text(stringResource(displayedTarget.title), style = MaterialTheme.typography.titleLarge,
                                    modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite })
                                Text(stringResource(displayedTarget.body), style = MaterialTheme.typography.bodyMedium)
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.onboarding_skip)) }
                                    Button(
                                        onClick = onNext,
                                        enabled = displayedStep == step,
                                    ) {
                                        Text(stringResource(if (displayedStep == TutorialTarget.entries.lastIndex) R.string.tutorial_finish else R.string.onboarding_start))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Posiziona la finestra sul root dell’app, conservando le coordinate dei bersagli. */
private object TutorialPopupPosition : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize,
    ): IntOffset = IntOffset(anchorBounds.left, anchorBounds.top)
}

/** Identificativi stabili per le verifiche della guida. */
object HomeTutorialTestTags {
    const val Message = "home_tutorial_message"
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
