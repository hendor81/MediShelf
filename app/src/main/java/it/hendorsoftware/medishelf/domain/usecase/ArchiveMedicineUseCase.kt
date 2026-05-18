package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import javax.inject.Inject

/**
 * Sposta un medicinale nell'archivio logico.
 *
 * L'archiviazione e l'azione preferita per rimuovere una voce dall'inventario
 * attivo senza perderne i dati storici locali.
 */
class ArchiveMedicineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Archivia il medicinale identificato.
     *
     * @param id identificativo domain della voce da archiviare.
     */
    suspend operator fun invoke(id: MedicineId) {
        medicineRepository.archiveMedicine(id)
    }
}
