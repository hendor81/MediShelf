package it.hendorsoftware.medishelf.data.mapper

import it.hendorsoftware.medishelf.data.local.entity.MedicineEntity
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import java.time.Instant
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test unitari del mapper tra entity Room e modello domain dei medicinali.
 */
class MedicineMapperTest {

    private val mapper = MedicineMapper()

    /**
     * Verifica il mapping completo da Room verso il dominio.
     */
    @Test
    fun shouldMapEntityToDomainWithQuantityAndExpirationDate() {
        val domain = mapper.toDomain(baseEntity())

        assertEquals(MedicineId(7L), domain.id)
        assertEquals("Paracetamolo", domain.name)
        assertEquals("Compresse", domain.packageForm)
        assertEquals(12.0, requireNotNull(domain.quantity).amount, 0.0)
        assertEquals("compresse", domain.quantity.unit)
        assertEquals(2.0, requireNotNull(domain.quantity.lowStockThreshold), 0.0)
        assertEquals(LocalDate.of(2026, 12, 31), domain.expirationDate)
        assertEquals("Bagno", domain.storageLocation)
        assertEquals("Dopo pranzo", domain.notes)
    }

    /**
     * Verifica che quantita e scadenza opzionali restino assenti nel domain.
     */
    @Test
    fun shouldMapEntityToDomainWithMissingOptionalQuantityAndExpirationDate() {
        val domain = mapper.toDomain(
            baseEntity(
                quantity = null,
                quantityUnit = null,
                lowStockThreshold = null,
                expirationDate = null,
            ),
        )

        assertNull(domain.quantity)
        assertNull(domain.expirationDate)
    }

    /**
     * Verifica il mapping completo dal domain verso Room.
     */
    @Test
    fun shouldMapDomainToEntityWithQuantityAndExpirationDate() {
        val entity = mapper.toEntity(baseMedicine())

        assertEquals(7L, entity.id)
        assertEquals("Paracetamolo", entity.name)
        assertEquals("Compresse", entity.packageForm)
        assertEquals(12.0, requireNotNull(entity.quantity), 0.0)
        assertEquals("compresse", entity.quantityUnit)
        assertEquals(2.0, requireNotNull(entity.lowStockThreshold), 0.0)
        assertEquals(LocalDate.of(2026, 12, 31), entity.expirationDate)
        assertEquals("Bagno", entity.storageLocation)
        assertEquals("Dopo pranzo", entity.notes)
    }

    /**
     * Verifica che una quantita assente non lasci unita o soglia persistite.
     */
    @Test
    fun shouldMapDomainToEntityWithoutQuantityDetailsWhenQuantityIsMissing() {
        val entity = mapper.toEntity(
            baseMedicine(
                quantity = null,
                expirationDate = null,
            ),
        )

        assertNull(entity.quantity)
        assertNull(entity.quantityUnit)
        assertNull(entity.lowStockThreshold)
        assertNull(entity.expirationDate)
    }

    /**
     * Verifica che i dati di archiviazione non vengano persi nel mapping bidirezionale.
     */
    @Test
    fun shouldKeepArchiveFieldsWhenMappingBothDirections() {
        val archivedAt = Instant.parse("2026-05-18T12:00:00Z")
        val archivedEntity = baseEntity(isArchived = true, archivedAt = archivedAt)
        val domain = mapper.toDomain(archivedEntity)
        val entity = mapper.toEntity(domain)

        assertTrue(domain.isArchived)
        assertEquals(archivedAt, domain.archivedAt)
        assertTrue(entity.isArchived)
        assertEquals(archivedAt, entity.archivedAt)
    }

    private fun baseEntity(
        quantity: Double? = 12.0,
        quantityUnit: String? = "compresse",
        lowStockThreshold: Double? = 2.0,
        expirationDate: LocalDate? = LocalDate.of(2026, 12, 31),
        isArchived: Boolean = false,
        archivedAt: Instant? = null,
    ): MedicineEntity {
        val now = Instant.parse("2026-05-18T10:00:00Z")

        return MedicineEntity(
            id = 7L,
            name = "Paracetamolo",
            packageForm = "Compresse",
            quantity = quantity,
            quantityUnit = quantityUnit,
            expirationDate = expirationDate,
            storageLocation = "Bagno",
            notes = "Dopo pranzo",
            lowStockThreshold = lowStockThreshold,
            isArchived = isArchived,
            createdAt = now,
            updatedAt = now,
            archivedAt = archivedAt,
        )
    }

    private fun baseMedicine(
        quantity: QuantityInfo? = QuantityInfo(
            amount = 12.0,
            unit = "compresse",
            lowStockThreshold = 2.0,
        ),
        expirationDate: LocalDate? = LocalDate.of(2026, 12, 31),
    ): Medicine {
        val now = Instant.parse("2026-05-18T10:00:00Z")

        return Medicine(
            id = MedicineId(7L),
            name = "Paracetamolo",
            packageForm = "Compresse",
            quantity = quantity,
            expirationDate = expirationDate,
            storageLocation = "Bagno",
            notes = "Dopo pranzo",
            isArchived = false,
            createdAt = now,
            updatedAt = now,
            archivedAt = null,
        )
    }
}
