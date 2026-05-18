package it.hendorsoftware.medishelf.domain.usecase

/**
 * Valida e normalizza il nome del medicinale prima del salvataggio.
 *
 * La regola resta nel domain/use case cosi ViewModel e UI possono delegare
 * la verifica minima senza conoscere dettagli di persistenza.
 */
internal object MedicineNameValidator {

    /**
     * Rimuove gli spazi esterni e verifica che il nome resti valorizzato.
     *
     * @param name nome inserito o modificato dall'utente.
     * @return nome normalizzato, pronto per il modello domain.
     * @throws IllegalArgumentException se il nome e vuoto dopo il trimming.
     */
    fun normalize(name: String): String {
        val trimmedName = name.trim()

        require(trimmedName.isNotEmpty()) { "Medicine name cannot be blank." }

        return trimmedName
    }
}
