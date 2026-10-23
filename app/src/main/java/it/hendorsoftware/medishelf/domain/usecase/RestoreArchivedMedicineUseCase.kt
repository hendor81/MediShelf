package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import java.time.Instant
import javax.inject.Inject

/** Ripristina una voce archiviata conservandone identificativo, quantità e scadenza. */
class RestoreArchivedMedicineUseCase @Inject constructor(
    private val repository: MedicineRepository,
) {
    /** Restituisce la voce ripristinata, oppure null se non esiste più. */
    suspend operator fun invoke(id: MedicineId): Medicine? {
        val medicine = repository.getMedicineById(id) ?: return null
        if (!medicine.isArchived) return medicine
        val restored = medicine.copy(isArchived = false, archivedAt = null, updatedAt = Instant.now())
        repository.saveMedicine(restored)
        return restored
    }
}
