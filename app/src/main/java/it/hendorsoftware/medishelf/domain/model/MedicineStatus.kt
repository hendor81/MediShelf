package it.hendorsoftware.medishelf.domain.model

/**
 * Stato derivato di una voce dell'inventario.
 *
 * Lo stato non rappresenta una decisione clinica e non va persistito come fonte
 * primaria: deve essere calcolato dal dominio a partire da quantita, scadenza,
 * soglie e archiviazione.
 */
enum class MedicineStatus {
    /**
     * Medicinale attivo senza criticita note.
     */
    VALID,

    /**
     * Medicinale attivo con scadenza entro la soglia configurata.
     */
    EXPIRING_SOON,

    /**
     * Medicinale attivo con data di scadenza gia superata.
     */
    EXPIRED,

    /**
     * Medicinale attivo con quantita nota pari a zero.
     */
    OUT_OF_STOCK,

    /**
     * Medicinale attivo per cui non e stata indicata una data di scadenza.
     */
    UNKNOWN_EXPIRATION,

    /**
     * Medicinale archiviato ed escluso dall'inventario attivo.
     */
    ARCHIVED,
}
