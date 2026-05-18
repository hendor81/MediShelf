package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import javax.inject.Inject

/**
 * Elimina definitivamente un medicinale gia confermato dall'utente.
 *
 * Il use case resta separato dall'archiviazione per mantenere esplicita
 * l'operazione distruttiva nei futuri ViewModel.
 */
class DeleteMedicineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Cancella fisicamente la voce identificata.
     *
     * @param id identificativo domain della voce da eliminare.
     */
    suspend operator fun invoke(id: MedicineId) {
        medicineRepository.deleteMedicine(id)
    }
}
