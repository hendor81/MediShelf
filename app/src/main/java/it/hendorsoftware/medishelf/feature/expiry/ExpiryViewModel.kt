package it.hendorsoftware.medishelf.feature.expiry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.core.common.MediShelfDefaults
import it.hendorsoftware.medishelf.core.designsystem.component.MedicineStatusBadgeStatus
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import it.hendorsoftware.medishelf.domain.usecase.GetActiveMedicinesUseCase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel della schermata Scadenzario.
 *
 * Osserva i medicinali attivi e li organizza nelle sezioni richieste dalla
 * feature: in scadenza, scaduti e senza data. Il calcolo dello stato resta nel
 * dominio tramite [MedicineStatusCalculator].
 */
@HiltViewModel
class ExpiryViewModel @Inject constructor(
    private val getActiveMedicinesUseCase: GetActiveMedicinesUseCase,
    private val statusCalculator: MedicineStatusCalculator,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExpiryUiState())

    /**
     * Stato osservabile dalla route Compose.
     *
     * @return stream dello stato corrente dello Scadenzario.
     */
    val uiState: StateFlow<ExpiryUiState> = _uiState.asStateFlow()

    init {
        observeExpirySections()
    }

    private fun observeExpirySections() {
        viewModelScope.launch {
            getActiveMedicinesUseCase().collect { medicines ->
                _uiState.value = medicines.toExpiryUiState()
            }
        }
    }

    private fun List<Medicine>.toExpiryUiState(): ExpiryUiState {
        val medicinesByStatus = map { medicine ->
            medicine to statusCalculator.calculate(
                medicine = medicine,
                expiringThresholdDays = MediShelfDefaults.ExpiringThresholdDays,
            )
        }

        return ExpiryUiState(
            isLoading = false,
            expiringMedicines = medicinesByStatus
                .filter { (_, status) -> status == MedicineStatus.EXPIRING }
                .sortedByExpirationDate()
                .map { (medicine, status) -> medicine.toUiModel(status) },
            expiredMedicines = medicinesByStatus
                .filter { (_, status) -> status == MedicineStatus.EXPIRED }
                .sortedByExpirationDate()
                .map { (medicine, status) -> medicine.toUiModel(status) },
            noExpirationMedicines = medicinesByStatus
                .filter { (_, status) -> status == MedicineStatus.NO_EXPIRATION_DATE }
                .sortedWith(
                    compareBy(
                        { (medicine, _) -> medicine.name.lowercase() },
                        { (medicine, _) -> medicine.name },
                    ),
                )
                .map { (medicine, status) -> medicine.toUiModel(status) },
        )
    }

    private fun List<Pair<Medicine, MedicineStatus>>.sortedByExpirationDate(): List<Pair<Medicine, MedicineStatus>> =
        sortedWith(
            compareBy<Pair<Medicine, MedicineStatus>> { (medicine, _) ->
                // Le sezioni con data usano un ordinamento cronologico stabile e leggibile.
                medicine.expirationDate ?: LocalDate.MAX
            }.thenBy { (medicine, _) -> medicine.name.lowercase() },
        )

    private fun Medicine.toUiModel(status: MedicineStatus): ExpiryMedicineItemUiModel =
        ExpiryMedicineItemUiModel(
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
