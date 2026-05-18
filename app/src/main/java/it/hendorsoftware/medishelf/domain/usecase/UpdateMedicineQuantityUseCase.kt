package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import java.time.Instant
import javax.inject.Inject

/**
 * Aggiorna rapidamente la quantita residua di un medicinale quando e gia nota.
 *
 * Mantiene la quantita opzionale: se l'utente non l'ha indicata, il use case non
 * crea un valore fittizio e segnala al chiamante che serve un'azione esplicita.
 */
class UpdateMedicineQuantityUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Incrementa di una unita la quantita disponibile.
     *
     * @param id identificativo locale del medicinale da aggiornare.
     * @param updatedAt istante da registrare come ultimo aggiornamento.
     * @return esito dell'operazione, con il medicinale aggiornato quando salvato.
     */
    suspend fun increment(
        id: MedicineId,
        updatedAt: Instant = Instant.now(),
    ): MedicineQuantityUpdateResult = updateQuantity(
        id = id,
        updatedAt = updatedAt,
        transform = { currentAmount -> currentAmount + QUANTITY_STEP },
    )

    /**
     * Decrementa di una unita la quantita disponibile senza scendere sotto zero.
     *
     * @param id identificativo locale del medicinale da aggiornare.
     * @param updatedAt istante da registrare come ultimo aggiornamento.
     * @return esito dell'operazione, con stato dedicato se la quantita e gia zero.
     */
    suspend fun decrement(
        id: MedicineId,
        updatedAt: Instant = Instant.now(),
    ): MedicineQuantityUpdateResult {
        val medicine = medicineRepository.getMedicineById(id)
            ?: return MedicineQuantityUpdateResult.NotFound
        val quantity = medicine.quantity
            ?: return MedicineQuantityUpdateResult.MissingQuantity

        if (quantity.amount <= ZERO_QUANTITY) {
            return MedicineQuantityUpdateResult.AlreadyZero(medicine)
        }

        val updatedMedicine = medicine.copy(
            quantity = quantity.copy(
                // La soglia zero e una regola di dominio: il decremento rapido
                // non deve mai produrre valori negativi.
                amount = (quantity.amount - QUANTITY_STEP).coerceAtLeast(ZERO_QUANTITY),
            ),
            updatedAt = updatedAt,
        )

        medicineRepository.saveMedicine(updatedMedicine)

        return MedicineQuantityUpdateResult.Updated(updatedMedicine)
    }

    private suspend fun updateQuantity(
        id: MedicineId,
        updatedAt: Instant,
        transform: (Double) -> Double,
    ): MedicineQuantityUpdateResult {
        val medicine = medicineRepository.getMedicineById(id)
            ?: return MedicineQuantityUpdateResult.NotFound
        val quantity = medicine.quantity
            ?: return MedicineQuantityUpdateResult.MissingQuantity

        val updatedMedicine = medicine.copy(
            quantity = quantity.copy(amount = transform(quantity.amount)),
            updatedAt = updatedAt,
        )

        medicineRepository.saveMedicine(updatedMedicine)

        return MedicineQuantityUpdateResult.Updated(updatedMedicine)
    }

    private companion object {
        private const val QUANTITY_STEP = 1.0
        private const val ZERO_QUANTITY = 0.0
    }
}

/**
 * Esito dell'aggiornamento rapido della quantita.
 */
sealed interface MedicineQuantityUpdateResult {

    /**
     * Quantita aggiornata e salvata.
     *
     * @param medicine modello domain aggiornato.
     */
    data class Updated(val medicine: Medicine) : MedicineQuantityUpdateResult

    /**
     * Il medicinale esiste ma non ha una quantita impostata.
     */
    data object MissingQuantity : MedicineQuantityUpdateResult

    /**
     * Il medicinale non esiste piu nel repository locale.
     */
    data object NotFound : MedicineQuantityUpdateResult

    /**
     * Il decremento non e stato applicato perche la quantita e gia zero.
     *
     * @param medicine modello domain rimasto invariato.
     */
    data class AlreadyZero(val medicine: Medicine) : MedicineQuantityUpdateResult
}
