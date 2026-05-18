package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import javax.inject.Inject

/**
 * Recupera una singola voce dell'inventario tramite identificativo domain.
 *
 * Serve le schermate di dettaglio e modifica senza esporre query Room ai layer UI.
 */
class GetMedicineByIdUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Cerca un medicinale per id.
     *
     * @param id identificativo domain della voce richiesta.
     * @return medicinale trovato, oppure null quando l'id non e presente.
     */
    suspend operator fun invoke(id: MedicineId): Medicine? =
        medicineRepository.getMedicineById(id)
}
