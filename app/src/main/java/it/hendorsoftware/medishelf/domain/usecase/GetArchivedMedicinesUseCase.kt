package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Recupera lo stream dei medicinali archiviati.
 *
 * Mantiene la distinzione tra inventario attivo e archivio nel layer domain,
 * pronta per i futuri ViewModel dell'area Archivio.
 */
class GetArchivedMedicinesUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Osserva i medicinali esclusi dall'inventario attivo.
     *
     * @return stream dei medicinali archiviati fornito dal repository domain.
     */
    operator fun invoke(): Flow<List<Medicine>> = medicineRepository.observeArchivedMedicines()
}
