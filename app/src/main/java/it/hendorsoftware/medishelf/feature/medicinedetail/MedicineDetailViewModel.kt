package it.hendorsoftware.medishelf.feature.medicinedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.ArchiveMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.DeleteMedicineUseCase
import it.hendorsoftware.medishelf.domain.usecase.GetMedicineByIdUseCase
import it.hendorsoftware.medishelf.domain.usecase.MedicineQuantityUpdateResult
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateMedicineQuantityUseCase
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel della schermata Dettaglio medicinale.
 *
 * Carica la voce tramite use case, mappa i dati in uno stato UI stabile e
 * coordina azioni distinte per archiviazione e cancellazione definitiva.
 */
@HiltViewModel
class MedicineDetailViewModel @Inject constructor(
    private val getMedicineByIdUseCase: GetMedicineByIdUseCase,
    private val archiveMedicineUseCase: ArchiveMedicineUseCase,
    private val deleteMedicineUseCase: DeleteMedicineUseCase,
    private val updateMedicineQuantityUseCase: UpdateMedicineQuantityUseCase,
    private val observeUserSettingsUseCase: ObserveUserSettingsUseCase,
    private val statusCalculator: MedicineStatusCalculator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineDetailUiState())
    private var currentMedicineId: MedicineId? = null

    /**
     * Stato osservabile dalla route Compose.
     *
     * @return stream dello stato corrente del dettaglio.
     */
    val uiState: StateFlow<MedicineDetailUiState> = _uiState.asStateFlow()

    /**
     * Carica il dettaglio associato all'identificativo di navigazione.
     *
     * @param medicineId valore ricevuto dalla route `MedicineDetail`.
     */
    fun loadMedicine(medicineId: String) {
        val id = medicineId.toLongOrNull()?.let(::MedicineId)

        if (id == null) {
            currentMedicineId = null
            _uiState.value = MedicineDetailUiState(
                isLoading = false,
                isNotFound = true,
            )
            return
        }

        currentMedicineId = id

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isLoading = true,
                    isNotFound = false,
                    medicine = null,
                    isDeleteDialogVisible = false,
                    hasArchiveCompleted = false,
                    hasDeleteCompleted = false,
                )
            }

            val medicine = getMedicineByIdUseCase(id)
            val settings = observeUserSettingsUseCase().first()
            _uiState.value = if (medicine == null) {
                currentMedicineId = null
                MedicineDetailUiState(
                    isLoading = false,
                    isNotFound = true,
                )
            } else {
                MedicineDetailUiState(
                    isLoading = false,
                    medicine = medicine.toUiModel(settings.expiringThresholdDays),
                )
            }
        }
    }

    /**
     * Incrementa rapidamente la quantita, se gia indicata dall'utente.
     */
    fun onQuantityIncrementClick() {
        updateQuantity { id -> updateMedicineQuantityUseCase.increment(id) }
    }

    /**
     * Decrementa rapidamente la quantita senza consentire valori negativi.
     */
    fun onQuantityDecrementClick() {
        updateQuantity { id -> updateMedicineQuantityUseCase.decrement(id) }
    }

    /**
     * Consuma il feedback una volta mostrato dalla UI.
     */
    fun onQuantityFeedbackShown() {
        _uiState.update { state -> state.copy(quantityFeedback = null) }
    }

    /**
     * Mostra il dialog di conferma prima della cancellazione definitiva.
     */
    fun onDeleteClick() {
        _uiState.update { state ->
            state.copy(isDeleteDialogVisible = true)
        }
    }

    /**
     * Nasconde il dialog senza cancellare la voce.
     */
    fun onDeleteDismissed() {
        _uiState.update { state ->
            state.copy(isDeleteDialogVisible = false)
        }
    }

    /**
     * Archivia logicamente la voce corrente.
     *
     * L'archiviazione resta separata dalla cancellazione fisica e viene usata
     * come azione preferita per rimuovere un elemento dall'inventario attivo.
     */
    fun onArchiveClick() {
        val id = currentMedicineId ?: return

        viewModelScope.launch {
            _uiState.update { state -> state.copy(isActionInProgress = true) }
            archiveMedicineUseCase(id)
            _uiState.update { state ->
                state.copy(
                    isActionInProgress = false,
                    hasArchiveCompleted = true,
                    medicine = state.medicine?.copy(
                        status = MedicineStatusBadgeStatus.Archived,
                        isArchived = true,
                    ),
                )
            }
        }
    }

    /**
     * Cancella definitivamente la voce corrente dopo conferma esplicita.
     */
    fun onDeleteConfirmed() {
        val id = currentMedicineId ?: return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(
                    isActionInProgress = true,
                    isDeleteDialogVisible = false,
                )
            }
            deleteMedicineUseCase(id)
            _uiState.update { state ->
                state.copy(
                    isActionInProgress = false,
                    hasDeleteCompleted = true,
                )
            }
        }
    }

    private fun updateQuantity(
        action: suspend (MedicineId) -> MedicineQuantityUpdateResult,
    ) {
        val id = currentMedicineId ?: return

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isActionInProgress = true, quantityFeedback = null)
            }

            when (val result = action(id)) {
                is MedicineQuantityUpdateResult.Updated -> {
                    val expiringThresholdDays = observeUserSettingsUseCase()
                        .first()
                        .expiringThresholdDays
                    _uiState.update { state ->
                        state.copy(
                            isActionInProgress = false,
                            medicine = result.medicine.toUiModel(expiringThresholdDays),
                            quantityFeedback = MedicineDetailQuantityFeedback.Updated,
                        )
                    }
                }

                is MedicineQuantityUpdateResult.AlreadyZero -> {
                    val expiringThresholdDays = observeUserSettingsUseCase()
                        .first()
                        .expiringThresholdDays
                    _uiState.update { state ->
                        state.copy(
                            isActionInProgress = false,
                            medicine = result.medicine.toUiModel(expiringThresholdDays),
                            quantityFeedback = MedicineDetailQuantityFeedback.AlreadyZero,
                        )
                    }
                }

                MedicineQuantityUpdateResult.MissingQuantity -> {
                    _uiState.update { state ->
                        state.copy(
                            isActionInProgress = false,
                            quantityFeedback = MedicineDetailQuantityFeedback.MissingQuantity,
                        )
                    }
                }

                MedicineQuantityUpdateResult.NotFound -> {
                    currentMedicineId = null
                    _uiState.update { state ->
                        state.copy(
                            isActionInProgress = false,
                            isNotFound = true,
                            medicine = null,
                        )
                    }
                }
            }
        }
    }

    private fun Medicine.toUiModel(expiringThresholdDays: Int): MedicineDetailUiModel {
        val status = if (isArchived) {
            MedicineStatusBadgeStatus.Archived
        } else {
            statusCalculator.calculate(
                medicine = this,
                expiringThresholdDays = expiringThresholdDays,
            ).toBadgeStatus()
        }

        return MedicineDetailUiModel(
            id = id.value.toString(),
            name = name,
            packageForm = packageForm,
            status = status,
            quantity = quantity?.let { info ->
                listOfNotNull(info.amount.toDisplayText(), info.unit)
                    .joinToString(separator = " ")
            },
            isQuantityAtZero = quantity?.amount == ZERO_QUANTITY,
            expirationDate = expirationDate?.format(ExpirationDateFormatter),
            storageLocation = storageLocation,
            notes = notes,
            isArchived = isArchived,
        )
    }

    private fun MedicineStatus.toBadgeStatus(): MedicineStatusBadgeStatus = when (this) {
        MedicineStatus.VALID -> MedicineStatusBadgeStatus.Valid
        MedicineStatus.EXPIRING -> MedicineStatusBadgeStatus.ExpiringSoon
        MedicineStatus.EXPIRED -> MedicineStatusBadgeStatus.Expired
        MedicineStatus.OUT_OF_STOCK -> MedicineStatusBadgeStatus.OutOfStock
        MedicineStatus.NO_EXPIRATION_DATE -> MedicineStatusBadgeStatus.NoExpiration
    }

    private fun Double.toDisplayText(): String =
        if (this % WHOLE_NUMBER_DIVISOR == 0.0) {
            toLong().toString()
        } else {
            toString()
        }

    private companion object {
        private const val WHOLE_NUMBER_DIVISOR = 1.0
        private const val ZERO_QUANTITY = 0.0
        private val ExpirationDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}
