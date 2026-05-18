package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

/**
 * Test unitari per i use case fondazionali sui medicinali.
 */
class MedicineUseCaseTest {

    private val now = Instant.parse("2026-05-18T10:00:00Z")
    private val later = Instant.parse("2026-05-18T11:00:00Z")

    /**
     * Verifica che l'inserimento rifiuti nomi vuoti prima di salvare.
     */
    @Test
    fun shouldRejectBlankNameWhenAddingMedicine() = runTest {
        val repository = FakeMedicineRepository()
        val useCase = AddMedicineUseCase(repository)

        try {
            useCase(name = "   ", createdAt = now)
            fail("Il nome vuoto deve essere rifiutato.")
        } catch (exception: IllegalArgumentException) {
            assertEquals("Medicine name cannot be blank.", exception.message)
        }

        assertEquals(0, repository.savedMedicines.size)
    }

    /**
     * Verifica che l'inserimento normalizzi il nome e deleghi al repository.
     */
    @Test
    fun shouldTrimNameWhenAddingMedicine() = runTest {
        val repository = FakeMedicineRepository()
        val useCase = AddMedicineUseCase(repository)

        val medicine = useCase(
            name = "  Paracetamolo  ",
            quantity = QuantityInfo(
                amount = 12.0,
                unit = "compresse",
                lowStockThreshold = 2.0,
            ),
            expirationDate = LocalDate.of(2026, 12, 31),
            createdAt = now,
        )

        assertEquals("Paracetamolo", medicine.name)
        assertEquals(listOf(medicine), repository.savedMedicines)
    }

    /**
     * Verifica che la modifica salvi il medicinale normalizzato.
     */
    @Test
    fun shouldUpdateMedicineUsingTrimmedName() = runTest {
        val repository = FakeMedicineRepository()
        val useCase = UpdateMedicineUseCase(repository)
        val editedMedicine = baseMedicine(name = "  Ibuprofene  ")

        val updatedMedicine = useCase(editedMedicine, updatedAt = later)

        assertEquals("Ibuprofene", updatedMedicine.name)
        assertEquals(later, updatedMedicine.updatedAt)
        assertEquals(listOf(updatedMedicine), repository.savedMedicines)
    }

    /**
     * Verifica che l'archiviazione passi dal repository domain e aggiorni lo stream archivio.
     */
    @Test
    fun shouldArchiveMedicineThroughRepository() = runTest {
        val repository = FakeMedicineRepository(initialMedicines = listOf(baseMedicine()))
        val useCase = ArchiveMedicineUseCase(repository)

        useCase(MedicineId(1L))

        val activeMedicines = repository.observeActiveMedicines().first()
        val archivedMedicines = repository.observeArchivedMedicines().first()

        assertEquals(emptyList<Medicine>(), activeMedicines)
        assertEquals(MedicineId(1L), archivedMedicines.single().id)
    }

    /**
     * Verifica che la cancellazione definitiva passi dal repository domain.
     */
    @Test
    fun shouldDeleteMedicineThroughRepository() = runTest {
        val repository = FakeMedicineRepository(initialMedicines = listOf(baseMedicine()))
        val useCase = DeleteMedicineUseCase(repository)

        useCase(MedicineId(1L))

        assertEquals(emptyList<Medicine>(), repository.observeActiveMedicines().first())
    }

    /**
     * Verifica che i use case di lettura espongano gli stream del repository.
     */
    @Test
    fun shouldExposeActiveAndArchivedMedicinesFromRepository() = runTest {
        val activeMedicine = baseMedicine(id = MedicineId(1L), name = "Paracetamolo")
        val archivedMedicine = baseMedicine(
            id = MedicineId(2L),
            name = "Ibuprofene",
            isArchived = true,
            archivedAt = later,
        )
        val repository = FakeMedicineRepository(
            initialMedicines = listOf(activeMedicine, archivedMedicine),
        )

        assertEquals(
            listOf(activeMedicine),
            GetActiveMedicinesUseCase(repository)().first(),
        )
        assertEquals(
            listOf(archivedMedicine),
            GetArchivedMedicinesUseCase(repository)().first(),
        )
        assertEquals(
            archivedMedicine,
            GetMedicineByIdUseCase(repository)(MedicineId(2L)),
        )
        assertNull(GetMedicineByIdUseCase(repository)(MedicineId(99L)))
    }

    /**
     * Verifica che il fake riusabile pubblichi aggiornamenti sugli stream Flow.
     */
    @Test
    fun shouldPublishUpdatedMedicinesFromReusableFakeRepository() = runTest {
        val repository = FakeMedicineRepository()
        val medicine = baseMedicine(name = "Magnesio")

        repository.setMedicines(listOf(medicine))

        assertEquals(listOf(medicine), repository.observeActiveMedicines().first())
        assertEquals(listOf(medicine), repository.currentMedicines())
    }

    private fun baseMedicine(
        id: MedicineId = MedicineId(1L),
        name: String = "Paracetamolo",
        isArchived: Boolean = false,
        archivedAt: Instant? = null,
    ): Medicine = Medicine(
        id = id,
        name = name,
        packageForm = "Compresse",
        quantity = QuantityInfo(
            amount = 12.0,
            unit = "compresse",
            lowStockThreshold = 2.0,
        ),
        expirationDate = LocalDate.of(2026, 12, 31),
        storageLocation = "Bagno",
        notes = null,
        isArchived = isArchived,
        createdAt = now,
        updatedAt = now,
        archivedAt = archivedAt,
    )

}
