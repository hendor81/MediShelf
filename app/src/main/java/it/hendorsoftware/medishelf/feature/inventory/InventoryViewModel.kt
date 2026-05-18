package it.hendorsoftware.medishelf.feature.inventory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.core.common.MediShelfDefaults
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.GetActiveMedicinesUseCase
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel della schermata Inventario.
 *
 * Osserva i medicinali attivi tramite use case e li converte in modelli UI
 * compatti, mantenendo il calcolo dello stato nel dominio.
 */
@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val getActiveMedicinesUseCase: GetActiveMedicinesUseCase,
    private val statusCalculator: MedicineStatusCalculator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState())

    /**
     * Stato osservabile dalla route Compose.
     *
     * @return stream dello stato corrente dell'inventario.
     */
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        observeActiveMedicines()
    }

    private fun observeActiveMedicines() {
        viewModelScope.launch {
            getActiveMedicinesUseCase().collect { medicines ->
                _uiState.value = InventoryUiState(
                    isLoading = false,
                    medicines = medicines.map { medicine -> medicine.toUiModel() },
                )
            }
        }
    }

    private fun Medicine.toUiModel(): InventoryMedicineItemUiModel {
        val status = statusCalculator.calculate(
            medicine = this,
            expiringThresholdDays = MediShelfDefaults.ExpiringThresholdDays,
        )

        return InventoryMedicineItemUiModel(
            id = id.value.toString(),
            name = name,
            packageForm = packageForm,
            status = status.toBadgeStatus(),
            expirationDate = expirationDate?.format(ExpirationDateFormatter),
            quantity = quantity?.let { info ->
                listOfNotNull(info.amount.toDisplayText(), info.unit)
                    .joinToString(separator = " ")
            },
            storageLocation = storageLocation,
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
        private val ExpirationDateFormatter: DateTimeFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}
