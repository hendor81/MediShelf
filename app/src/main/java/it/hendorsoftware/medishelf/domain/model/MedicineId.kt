package it.hendorsoftware.medishelf.domain.model

/**
 * Identificativo stabile di un medicinale nel dominio.
 *
 * @param value valore tecnico locale associato alla voce di inventario.
 *
 * Il wrapper evita di confondere l'id del medicinale con altri identificativi
 * numerici usati nei layer data o UI.
 */
@JvmInline
value class MedicineId(val value: Long)
