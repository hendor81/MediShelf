package it.hendorsoftware.medishelf.domain.model

import java.time.Instant
import java.time.LocalDate

/**
 * Voce dell'inventario dei medicinali gestita dal dominio.
 *
 * @param id identificativo stabile locale della voce.
 * @param name nome del medicinale o prodotto; non puo essere vuoto.
 * @param packageForm formato o confezione, per esempio compresse, sciroppo o spray.
 * @param quantity quantita disponibile, opzionale per ridurre l'attrito di inserimento.
 * @param expirationDate data di scadenza, opzionale quando l'utente non la conosce.
 * @param storageLocation luogo libero di conservazione.
 * @param notes note libere dell'utente.
 * @param isArchived indica se la voce e esclusa dall'inventario attivo.
 * @param createdAt istante di creazione della voce.
 * @param updatedAt istante dell'ultima modifica nota.
 * @param archivedAt istante di archiviazione, valorizzabile solo per voci archiviate.
 *
 * @throws IllegalArgumentException se [name] e vuoto o se [archivedAt] e valorizzato
 * per una voce non archiviata.
 */
data class Medicine(
    val id: MedicineId,
    val name: String,
    val packageForm: String?,
    val quantity: QuantityInfo?,
    val expirationDate: LocalDate?,
    val storageLocation: String?,
    val notes: String?,
    val isArchived: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val archivedAt: Instant?,
) {

    init {
        require(name.isNotBlank()) { "Medicine name cannot be blank." }
        require(isArchived || archivedAt == null) {
            "Archived timestamp can be set only when the medicine is archived."
        }
    }
}
