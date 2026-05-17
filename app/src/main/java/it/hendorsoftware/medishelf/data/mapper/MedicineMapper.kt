package it.hendorsoftware.medishelf.data.mapper

import it.hendorsoftware.medishelf.data.local.entity.MedicineEntity
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import javax.inject.Inject

/**
 * Converte i medicinali tra rappresentazione Room e modello di dominio.
 *
 * Il mapper centralizza la gestione dei campi opzionali, in particolare
 * quantita e scadenza, evitando che entity Room entrino nei layer superiori.
 */
class MedicineMapper @Inject constructor() {

    /**
     * Converte una entity Room in modello di dominio.
     *
     * @param entity record persistito nel database locale.
     * @return medicinale usabile dal domain e dai futuri use case.
     */
    fun toDomain(entity: MedicineEntity): Medicine = Medicine(
        id = MedicineId(entity.id),
        name = entity.name,
        packageForm = entity.packageForm,
        quantity = entity.toQuantityInfo(),
        expirationDate = entity.expirationDate,
        storageLocation = entity.storageLocation,
        notes = entity.notes,
        isArchived = entity.isArchived,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        archivedAt = entity.archivedAt,
    )

    /**
     * Converte un modello di dominio in entity Room completa.
     *
     * @param medicine medicinale da salvare localmente.
     * @return entity pronta per insert o update nel DAO.
     */
    fun toEntity(medicine: Medicine): MedicineEntity = MedicineEntity(
        id = medicine.id.value,
        name = medicine.name,
        packageForm = medicine.packageForm,
        quantity = medicine.quantity?.amount,
        quantityUnit = medicine.quantity?.unit,
        expirationDate = medicine.expirationDate,
        storageLocation = medicine.storageLocation,
        notes = medicine.notes,
        lowStockThreshold = medicine.quantity?.lowStockThreshold,
        isArchived = medicine.isArchived,
        createdAt = medicine.createdAt,
        updatedAt = medicine.updatedAt,
        archivedAt = medicine.archivedAt,
    )

    private fun MedicineEntity.toQuantityInfo(): QuantityInfo? {
        // La quantita e opzionale: unita e soglia hanno senso solo quando esiste un valore.
        return quantity?.let { amount ->
            QuantityInfo(
                amount = amount,
                unit = quantityUnit,
                lowStockThreshold = lowStockThreshold,
            )
        }
    }
}
