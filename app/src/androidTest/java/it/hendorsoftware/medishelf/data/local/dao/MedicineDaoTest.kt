package it.hendorsoftware.medishelf.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import it.hendorsoftware.medishelf.data.local.database.MediShelfDatabase
import it.hendorsoftware.medishelf.data.local.entity.MedicineEntity
import java.time.Instant
import java.time.LocalDate
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Test strumentali del DAO Room dei medicinali.
 *
 * Usa un database in memoria per verificare query e persistenza senza toccare
 * il database reale dell'app.
 */
@RunWith(AndroidJUnit4::class)
class MedicineDaoTest {

    private lateinit var database: MediShelfDatabase
    private lateinit var dao: MedicineDao

    /**
     * Crea un database Room in memoria prima di ogni test.
     */
    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MediShelfDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.medicineDao()
    }

    /**
     * Chiude il database in memoria dopo ogni test.
     */
    @After
    fun tearDown() {
        database.close()
    }

    /**
     * Verifica inserimento e recupero diretto tramite id generato.
     */
    @Test
    fun shouldInsertMedicineAndReadItById() = runBlocking {
        val id = dao.insertMedicine(baseEntity(name = "Paracetamolo"))

        val savedMedicine = dao.getMedicineById(id)

        assertEquals("Paracetamolo", savedMedicine?.name)
        assertEquals(12.0, requireNotNull(savedMedicine?.quantity), 0.0)
        assertEquals(LocalDate.of(2026, 12, 31), savedMedicine?.expirationDate)
    }

    /**
     * Verifica che i campi opzionali possano restare null.
     */
    @Test
    fun shouldPersistMedicineWithNullableQuantityAndExpirationDate() = runBlocking {
        val id = dao.insertMedicine(
            baseEntity(
                quantity = null,
                quantityUnit = null,
                lowStockThreshold = null,
                expirationDate = null,
            ),
        )

        val savedMedicine = dao.getMedicineById(id)

        assertNull(savedMedicine?.quantity)
        assertNull(savedMedicine?.quantityUnit)
        assertNull(savedMedicine?.lowStockThreshold)
        assertNull(savedMedicine?.expirationDate)
    }

    /**
     * Verifica che l'osservazione degli attivi escluda le voci archiviate.
     */
    @Test
    fun shouldObserveOnlyActiveMedicines() = runBlocking {
        dao.insertMedicine(baseEntity(name = "Zinco"))
        dao.insertMedicine(baseEntity(name = "Aspirina", isArchived = true))

        val activeMedicines = dao.observeActiveMedicines().first()

        assertEquals(listOf("Zinco"), activeMedicines.map { it.name })
        assertFalse(activeMedicines.first().isArchived)
    }

    /**
     * Verifica che l'archiviazione logica aggiorni stato e timestamp.
     */
    @Test
    fun shouldArchiveMedicineWithoutDeletingIt() = runBlocking {
        val id = dao.insertMedicine(baseEntity())
        val archivedAt = Instant.parse("2026-05-18T12:00:00Z")

        dao.archiveMedicine(id = id, archivedAt = archivedAt)

        val savedMedicine = requireNotNull(dao.getMedicineById(id))
        assertTrue(savedMedicine.isArchived)
        assertEquals(archivedAt, savedMedicine.archivedAt)
        assertEquals(archivedAt, savedMedicine.updatedAt)
    }

    /**
     * Verifica che l'osservazione degli archiviati includa solo le voci archiviate.
     */
    @Test
    fun shouldObserveOnlyArchivedMedicines() = runBlocking {
        dao.insertMedicine(baseEntity(name = "Attivo"))
        dao.insertMedicine(baseEntity(name = "Archiviato", isArchived = true))

        val archivedMedicines = dao.observeArchivedMedicines().first()

        assertEquals(listOf("Archiviato"), archivedMedicines.map { it.name })
        assertTrue(archivedMedicines.first().isArchived)
    }

    /**
     * Verifica che la cancellazione fisica rimuova definitivamente la voce.
     */
    @Test
    fun shouldDeleteMedicinePhysicallyById() = runBlocking {
        val id = dao.insertMedicine(baseEntity())

        dao.deleteMedicineById(id)

        assertNull(dao.getMedicineById(id))
    }

    private fun baseEntity(
        name: String = "Ibuprofene",
        quantity: Double? = 12.0,
        quantityUnit: String? = "compresse",
        lowStockThreshold: Double? = 2.0,
        expirationDate: LocalDate? = LocalDate.of(2026, 12, 31),
        isArchived: Boolean = false,
    ): MedicineEntity {
        val now = Instant.parse("2026-05-18T10:00:00Z")
        val archivedAt = if (isArchived) now else null

        return MedicineEntity(
            name = name,
            packageForm = "Compresse",
            quantity = quantity,
            quantityUnit = quantityUnit,
            expirationDate = expirationDate,
            storageLocation = "Bagno",
            notes = null,
            lowStockThreshold = lowStockThreshold,
            isArchived = isArchived,
            createdAt = now,
            updatedAt = now,
            archivedAt = archivedAt,
        )
    }
}
