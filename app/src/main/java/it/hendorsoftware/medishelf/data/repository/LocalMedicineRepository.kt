package it.hendorsoftware.medishelf.data.repository

import it.hendorsoftware.medishelf.data.local.dao.MedicineDao
import it.hendorsoftware.medishelf.data.mapper.MedicineMapper
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementazione locale di [MedicineRepository] basata su Room.
 *
 * Coordina DAO e mapper senza introdurre calcoli di stato o regole di business:
 * quelle restano nel dominio e nei futuri use case.
 */
class LocalMedicineRepository @Inject constructor(
    private val medicineDao: MedicineDao,
    private val mapper: MedicineMapper,
) : MedicineRepository {

    /**
     * Osserva i medicinali attivi leggendo dal DAO locale.
     *
     * @return stream dei medicinali non archiviati convertiti in domain model.
     */
    override fun observeActiveMedicines(): Flow<List<Medicine>> =
        medicineDao.observeActiveMedicines().map { entities ->
            entities.map(mapper::toDomain)
        }

    /**
     * Osserva i medicinali archiviati leggendo dal DAO locale.
     *
     * @return stream dei medicinali archiviati convertiti in domain model.
     */
    override fun observeArchivedMedicines(): Flow<List<Medicine>> =
        medicineDao.observeArchivedMedicines().map { entities ->
            entities.map(mapper::toDomain)
        }

    /**
     * Recupera una singola voce tramite id locale.
     *
     * @param id identificativo domain della voce.
     * @return medicinale convertito, oppure null se assente.
     */
    override suspend fun getMedicineById(id: MedicineId): Medicine? =
        medicineDao.getMedicineById(id.value)?.let(mapper::toDomain)

    /**
     * Inserisce una nuova voce quando l'id e nullo per Room, altrimenti aggiorna l'esistente.
     *
     * @param medicine medicinale da persistere.
     */
    override suspend fun saveMedicine(medicine: Medicine) {
        val entity = mapper.toEntity(medicine)

        if (medicine.id.value == NEW_MEDICINE_ID) {
            medicineDao.insertMedicine(entity)
        } else {
            medicineDao.updateMedicine(entity)
        }
    }

    /**
     * Archivia logicamente una voce usando l'istante corrente come timestamp persistente.
     *
     * @param id identificativo domain della voce da archiviare.
     */
    override suspend fun archiveMedicine(id: MedicineId) {
        medicineDao.archiveMedicine(
            id = id.value,
            archivedAt = Instant.now(),
        )
    }

    /**
     * Cancella definitivamente una voce tramite id.
     *
     * @param id identificativo domain della voce da eliminare.
     */
    override suspend fun deleteMedicine(id: MedicineId) {
        medicineDao.deleteMedicineById(id.value)
    }

    private companion object {
        private const val NEW_MEDICINE_ID = 0L
    }
}
