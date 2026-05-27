package it.hendorsoftware.medishelf.feature.archive

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.usecase.GetArchivedMedicinesUseCase
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel della schermata Archivio.
 *
 * Osserva i medicinali archiviati tramite use case e li mappa in un modello UI
 * essenziale, mantenendo l'Archivio come area secondaria di consultazione.
 */
@HiltViewModel
class ArchiveViewModel @Inject constructor(
    private val getArchivedMedicinesUseCase: GetArchivedMedicinesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ArchiveUiState())

    /**
     * Stato osservabile dalla route Compose.
     *
     * @return stream dello stato corrente dell'archivio.
     */
    val uiState: StateFlow<ArchiveUiState> = _uiState.asStateFlow()

    init {
        observeArchivedMedicines()
    }

    private fun observeArchivedMedicines() {
        viewModelScope.launch {
            getArchivedMedicinesUseCase().collect { medicines ->
                _uiState.value = ArchiveUiState(
                    isLoading = false,
                    medicines = medicines.map { medicine -> medicine.toUiModel() },
                )
            }
        }
    }

    private fun Medicine.toUiModel(): ArchiveMedicineItemUiModel =
        ArchiveMedicineItemUiModel(
            id = id.value.toString(),
            name = name,
            packageForm = packageForm,
            expirationDate = expirationDate?.format(ExpirationDateFormatter),
            quantity = quantity?.let { info ->
                listOfNotNull(info.amount.toDisplayText(), info.unit)
                    .joinToString(separator = " ")
            },
            storageLocation = storageLocation,
        )

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
