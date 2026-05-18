package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
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
    fun shouldRejectBlankNameWhenAddingMedicine() {
        val repository = FakeMedicineRepository()
        val useCase = AddMedicineUseCase(repository)

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                useCase(name = "   ", createdAt = now)
            }
        }

        assertEquals(0, repository.savedMedicines.size)
    }

    /**
     * Verifica che l'inserimento normalizzi il nome e deleghi al repository.
     */
    @Test
    fun shouldTrimNameWhenAddingMedicine() = runBlocking {
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
    fun shouldUpdateMedicineUsingTrimmedName() = runBlocking {
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
    fun shouldArchiveMedicineThroughRepository() = runBlocking {
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
    fun shouldDeleteMedicineThroughRepository() = runBlocking {
        val repository = FakeMedicineRepository(initialMedicines = listOf(baseMedicine()))
        val useCase = DeleteMedicineUseCase(repository)

        useCase(MedicineId(1L))

        assertEquals(emptyList<Medicine>(), repository.observeActiveMedicines().first())
    }

    /**
     * Verifica che i use case di lettura espongano gli stream del repository.
     */
    @Test
    fun shouldExposeActiveAndArchivedMedicinesFromRepository() = runBlocking {
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

    private class FakeMedicineRepository(
        initialMedicines: List<Medicine> = emptyList(),
    ) : MedicineRepository {

        private val medicines = MutableStateFlow(initialMedicines)

        val savedMedicines = mutableListOf<Medicine>()

        override fun observeActiveMedicines(): Flow<List<Medicine>> =
            medicines.map { values -> values.filterNot(Medicine::isArchived) }

        override fun observeArchivedMedicines(): Flow<List<Medicine>> =
            medicines.map { values -> values.filter(Medicine::isArchived) }

        override suspend fun getMedicineById(id: MedicineId): Medicine? =
            medicines.value.firstOrNull { medicine -> medicine.id == id }

        override suspend fun saveMedicine(medicine: Medicine) {
            savedMedicines += medicine

            val currentMedicines = medicines.value.toMutableList()
            val currentIndex = currentMedicines.indexOfFirst { current ->
                current.id == medicine.id
            }

            if (currentIndex >= 0) {
                currentMedicines[currentIndex] = medicine
            } else {
                currentMedicines += medicine
            }

            medicines.value = currentMedicines
        }

        override suspend fun archiveMedicine(id: MedicineId) {
            medicines.value = medicines.value.map { medicine ->
                if (medicine.id == id) {
                    medicine.copy(
                        isArchived = true,
                        updatedAt = ARCHIVED_AT,
                        archivedAt = ARCHIVED_AT,
                    )
                } else {
                    medicine
                }
            }
        }

        override suspend fun deleteMedicine(id: MedicineId) {
            medicines.value = medicines.value.filterNot { medicine -> medicine.id == id }
        }

        private companion object {
            private val ARCHIVED_AT: Instant = Instant.parse("2026-05-18T12:00:00Z")
        }
    }
}
