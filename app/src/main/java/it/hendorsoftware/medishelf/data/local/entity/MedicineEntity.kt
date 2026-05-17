package it.hendorsoftware.medishelf.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate

/**
 * Rappresenta una voce dell'inventario salvata nel database locale Room.
 *
 * Lo stato del medicinale non e incluso per evitare incoerenze: resta derivato
 * dal dominio a partire da quantita, scadenza e archiviazione.
 */
@Entity(
    tableName = "medicines",
    indices = [
        Index(value = ["name"]),
        Index(value = ["expirationDate"]),
        Index(value = ["isArchived"]),
        Index(value = ["storageLocation"]),
    ],
)
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val packageForm: String?,
    val quantity: Double?,
    val quantityUnit: String?,
    val expirationDate: LocalDate?,
    val storageLocation: String?,
    val notes: String?,
    val lowStockThreshold: Double?,
    val isArchived: Boolean = false,
    val createdAt: Instant,
    val updatedAt: Instant,
    val archivedAt: Instant?,
)
