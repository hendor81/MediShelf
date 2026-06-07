package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Elimina definitivamente tutti i medicinali archiviati dopo conferma utente.
 *
 * @param medicineRepository repository domain dei medicinali.
 */
class ClearArchiveUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Cancella fisicamente le voci attualmente presenti in archivio.
     *
     * @return numero di medicinali eliminati.
     */
    suspend operator fun invoke(): Int {
        val archivedMedicines = medicineRepository.observeArchivedMedicines().first()
        archivedMedicines.forEach { medicine ->
            medicineRepository.deleteMedicine(medicine.id)
        }
        return archivedMedicines.size
    }
}
