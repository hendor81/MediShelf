package it.hendorsoftware.medishelf.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import it.hendorsoftware.medishelf.domain.usecase.GetHomeSummaryUseCase
import it.hendorsoftware.medishelf.domain.usecase.HomeAttentionMedicine
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel della Home / Dashboard.
 *
 * Osserva il riepilogo prodotto dal dominio e lo converte nel modello UI della
 * schermata, lasciando alle Composable solo il rendering e le callback utente.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeSummaryUseCase: GetHomeSummaryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())

    /**
     * Stato osservabile dalla route Compose della Home.
     *
     * @return stream dello stato corrente della dashboard.
     */
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeHomeSummary()
    }

    private fun observeHomeSummary() {
        viewModelScope.launch {
            getHomeSummaryUseCase().collect { summary ->
                _uiState.value = HomeUiState(
                    isLoading = false,
                    activeMedicineCount = summary.activeMedicineCount,
                    expiringMedicineCount = summary.expiringMedicineCount,
                    expiredMedicineCount = summary.expiredMedicineCount,
                    lowStockMedicineCount = summary.lowStockMedicineCount,
                    attentionItems = summary.attentionMedicines.map { item ->
                        item.toUiModel()
                    },
                )
            }
        }
    }

    private fun HomeAttentionMedicine.toUiModel(): HomeAttentionItemUiModel =
        medicine.toAttentionUiModel(
            status = status,
            isLowStock = isLowStock,
        )

    private fun Medicine.toAttentionUiModel(
        status: MedicineStatus,
        isLowStock: Boolean,
    ): HomeAttentionItemUiModel =
        HomeAttentionItemUiModel(
            id = id.value.toString(),
            name = name,
            packageForm = packageForm,
            expirationDate = expirationDate?.format(ExpirationDateFormatter),
            quantity = quantity?.let { info ->
                listOfNotNull(info.amount.toDisplayText(), info.unit)
                    .joinToString(separator = " ")
            },
            status = status.toBadgeStatus(isLowStock),
        )

    private fun MedicineStatus.toBadgeStatus(isLowStock: Boolean): MedicineStatusBadgeStatus =
        when {
            isLowStock && this == MedicineStatus.VALID -> MedicineStatusBadgeStatus.LowStock
            else -> toBadgeStatus()
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
        private val ExpirationDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}
