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
import kotlinx.coroutines.flow.combine
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
    private val searchQuery = MutableStateFlow("")
    private val selectedStatusFilter = MutableStateFlow(InventoryStatusFilter.All)

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
            combine(
                getActiveMedicinesUseCase(),
                searchQuery,
                selectedStatusFilter,
            ) { medicines, query, statusFilter ->
                val medicinesWithStatus = medicines.map { medicine ->
                    medicine to statusCalculator.calculate(
                        medicine = medicine,
                        expiringThresholdDays = MediShelfDefaults.ExpiringThresholdDays,
                    )
                }
                val filteredMedicines = medicinesWithStatus.filterByNameAndStatus(
                    query = query,
                    statusFilter = statusFilter,
                )
                InventoryUiState(
                    isLoading = false,
                    medicines = filteredMedicines.map { (medicine, status) ->
                        medicine.toUiModel(status)
                    },
                    searchQuery = query,
                    selectedStatusFilter = statusFilter,
                    hasActiveMedicines = medicines.isNotEmpty(),
                )
            }.collect { inventoryUiState ->
                _uiState.value = inventoryUiState
            }
        }
    }

    /**
     * Aggiorna la query testuale usata per cercare i medicinali per nome.
     *
     * @param query testo digitato dall'utente nel campo ricerca.
     */
    fun onSearchQueryChanged(query: String) {
        searchQuery.value = query
    }

    /**
     * Svuota il campo ricerca e ripristina la lista completa dei medicinali attivi.
     */
    fun onSearchQueryCleared() {
        searchQuery.value = ""
    }

    /**
     * Aggiorna il filtro di stato usato per restringere l'inventario.
     *
     * @param filter filtro selezionato nel controllo della schermata.
     */
    fun onStatusFilterSelected(filter: InventoryStatusFilter) {
        selectedStatusFilter.value = filter
    }

    private fun List<Pair<Medicine, MedicineStatus>>.filterByNameAndStatus(
        query: String,
        statusFilter: InventoryStatusFilter,
    ): List<Pair<Medicine, MedicineStatus>> {
        val normalizedQuery = query.trim()

        return filter { (medicine, status) ->
            // Il filtro resta qui, fuori dalla Composable, cosi la UI riceve una lista gia coerente.
            val matchesName = normalizedQuery.isBlank() ||
                medicine.name.contains(normalizedQuery, ignoreCase = true)
            val matchesStatus = statusFilter.includes(status)

            matchesName && matchesStatus
        }
    }

    private fun Medicine.toUiModel(status: MedicineStatus): InventoryMedicineItemUiModel =
        InventoryMedicineItemUiModel(
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
