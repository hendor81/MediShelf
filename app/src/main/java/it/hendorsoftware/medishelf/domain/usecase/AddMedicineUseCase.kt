package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

/**
 * Crea una nuova voce dell'inventario e la salva tramite repository domain.
 *
 * Il use case applica la validazione minima del nome e mantiene la nuova voce
 * nell'inventario attivo. I dettagli Room restano confinati nel layer data.
 */
class AddMedicineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Salva un nuovo medicinale partendo dai campi raccolti dal flusso di inserimento.
     *
     * @param name nome obbligatorio del medicinale; viene normalizzato con trimming.
     * @param packageForm formato o confezione indicata dall'utente.
     * @param quantity quantita opzionale tracciata per la voce.
     * @param expirationDate data di scadenza opzionale.
     * @param storageLocation luogo libero di conservazione.
     * @param notes note libere opzionali.
     * @param createdAt istante di creazione usato anche come primo aggiornamento.
     * @return medicinale domain salvato tramite repository.
     * @throws IllegalArgumentException se [name] e vuoto dopo il trimming.
     */
    suspend operator fun invoke(
        name: String,
        packageForm: String? = null,
        quantity: QuantityInfo? = null,
        expirationDate: LocalDate? = null,
        storageLocation: String? = null,
        notes: String? = null,
        createdAt: Instant = Instant.now(),
    ): Medicine {
        val medicine = Medicine(
            id = MedicineId(NEW_MEDICINE_ID),
            name = MedicineNameValidator.normalize(name),
            packageForm = packageForm,
            quantity = quantity,
            expirationDate = expirationDate,
            storageLocation = storageLocation,
            notes = notes,
            isArchived = false,
            createdAt = createdAt,
            updatedAt = createdAt,
            archivedAt = null,
        )

        medicineRepository.saveMedicine(medicine)

        return medicine
    }

    private companion object {
        private const val NEW_MEDICINE_ID = 0L
    }
}
