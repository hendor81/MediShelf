package it.hendorsoftware.medishelf.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.hendorsoftware.medishelf.data.local.database.MediShelfDatabase
import it.hendorsoftware.medishelf.data.mapper.MedicineMapper
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineId
import it.hendorsoftware.medishelf.domain.model.QuantityInfo
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/** Regressioni del repository reale, mapper e query Room su un database isolato in memoria. */
@RunWith(AndroidJUnit4::class)
class LocalMedicineRepositoryRegressionTest {
    private lateinit var database: MediShelfDatabase
    private lateinit var repository: LocalMedicineRepository

    /** Ogni test parte da un database vuoto e non accede ai dati installati dell'utente. */
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext<Context>(), MediShelfDatabase::class.java,
        ).build()
        repository = LocalMedicineRepository(database.medicineDao(), MedicineMapper())
    }

    /** Rilascia connessioni e risorse Room anche se il test fallisce. */
    @After
    fun tearDown() = database.close()

    /** Due nuovi salvataggi generano id distinti; una modifica conserva identità e campi null. */
    @Test
    fun shouldGenerateDistinctIdsAndUpdateWithoutDuplicatingMedicine() = runBlocking {
        repository.saveMedicine(medicine("Paracetamolo"))
        repository.saveMedicine(medicine("Ibuprofene"))
        val initial = repository.observeActiveMedicines().first()
        assertEquals(2, initial.size)
        assertEquals(2, initial.map { it.id }.distinct().size)
        assertTrue(initial.all { it.id.value > 0 })
        val saved = initial.single { it.name == "Paracetamolo" }
        assertEquals(medicine("Paracetamolo").copy(id = saved.id), repository.getMedicineById(saved.id))

        val edited = saved.copy(
            name = "Paracetamolo aggiornato", quantity = null, expirationDate = null,
            packageForm = null, storageLocation = null, notes = null, updatedAt = later,
        )
        repository.saveMedicine(edited)
        assertEquals(edited, repository.getMedicineById(saved.id))
        val active = repository.observeActiveMedicines().first()
        assertEquals(2, active.size)
        assertEquals(initial.single { it.name == "Ibuprofene" }, active.single { it.name == "Ibuprofene" })
        assertNull(repository.getMedicineById(MedicineId(Long.MAX_VALUE)))
    }

    /** Archiviare conserva i dati; ripristino e cancellazione si riflettono negli stream reali. */
    @Test
    fun shouldMoveMedicineBetweenActiveAndArchiveWithoutLosingData() = runBlocking {
        repository.saveMedicine(medicine("Paracetamolo"))
        repository.saveMedicine(medicine("Zinco"))
        val saved = repository.observeActiveMedicines().first().first()
        repository.archiveMedicine(saved.id)
        val archived = requireNotNull(repository.getMedicineById(saved.id))
        assertTrue(archived.isArchived)
        assertNotNull(archived.archivedAt)
        assertEquals(archived.archivedAt, archived.updatedAt)
        // Il timestamp di archiviazione è generato dal repository: si verifica
        // la coerenza dei campi, senza confrontarlo con l'orologio del dispositivo.
        assertEquals(saved, archived.copy(isArchived = false, archivedAt = null, updatedAt = saved.updatedAt))
        assertEquals(listOf("Zinco"), repository.observeActiveMedicines().first().map { it.name })
        assertEquals(listOf(archived), repository.observeArchivedMedicines().first())

        val restored = archived.copy(isArchived = false, archivedAt = null, updatedAt = later)
        repository.saveMedicine(restored)
        assertEquals(restored, repository.getMedicineById(saved.id))
        assertTrue(repository.observeArchivedMedicines().first().isEmpty())
        assertEquals(2, repository.observeActiveMedicines().first().size)
        repository.deleteMedicine(saved.id)
        assertNull(repository.getMedicineById(saved.id))
        assertEquals(listOf("Zinco"), repository.observeActiveMedicines().first().map { it.name })
    }

    /** Un osservatore già attivo riceve l'aggiornamento, non solo una nuova query iniziale. */
    @Test
    fun shouldPublishUpdatesToExistingCollector() = runBlocking {
        repository.saveMedicine(medicine("Paracetamolo"))
        val saved = repository.observeActiveMedicines().first().single()
        val edited = saved.copy(quantity = QuantityInfo(0.0, "compresse", 2.0), updatedAt = later)
        var updates = 0
        // first esegue il predicato nel collettore: il salvataggio avviene dopo
        // la prima emissione, senza sleep o ipotesi sui tempi di invalidazione Room.
        val observed = withTimeout(5_000) {
            repository.observeActiveMedicines().first { values ->
                updates++
                if (values.single() == saved) repository.saveMedicine(edited)
                values.single() == edited
            }
        }
        assertTrue(updates >= 2)
        assertEquals(listOf(edited), observed)
    }

    private companion object {
        val instant: Instant = Instant.parse("2026-05-18T10:00:00Z")
        val later: Instant = Instant.parse("2026-05-19T10:00:00Z")

        /** Campi valorizzati per intercettare perdite nel round trip mapper/Room. */
        fun medicine(name: String) = Medicine(
            id = MedicineId(0), name = name, packageForm = "Compresse",
            quantity = QuantityInfo(12.5, "compresse", 2.0), expirationDate = LocalDate.of(2028, 2, 29),
            storageLocation = "Bagno", notes = "Confezione iniziata", isArchived = false,
            createdAt = instant, updatedAt = instant, archivedAt = null,
        )
    }
}
