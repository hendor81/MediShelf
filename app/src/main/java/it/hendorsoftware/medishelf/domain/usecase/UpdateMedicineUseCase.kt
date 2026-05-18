package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import java.time.Instant
import javax.inject.Inject

/**
 * Aggiorna una voce esistente passando dal repository domain.
 *
 * Il use case normalizza il nome e aggiorna il timestamp applicativo prima di
 * delegare il salvataggio, lasciando al repository concreto solo la persistenza.
 */
class UpdateMedicineUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Salva una versione modificata del medicinale.
     *
     * @param medicine modello domain con i campi modificati.
     * @param updatedAt istante da registrare come ultimo aggiornamento.
     * @return medicinale normalizzato e inviato al repository.
     * @throws IllegalArgumentException se il nome e vuoto dopo il trimming.
     */
    suspend operator fun invoke(
        medicine: Medicine,
        updatedAt: Instant = Instant.now(),
    ): Medicine {
        val updatedMedicine = medicine.copy(
            name = MedicineNameValidator.normalize(medicine.name),
            updatedAt = updatedAt,
        )

        medicineRepository.saveMedicine(updatedMedicine)

        return updatedMedicine
    }
}
