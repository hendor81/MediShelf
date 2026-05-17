package it.hendorsoftware.medishelf.core.time

import java.time.LocalDate
import javax.inject.Inject

/**
 * Implementazione di produzione di [DateProvider] basata sull'orologio di sistema.
 */
class SystemDateProvider @Inject constructor() : DateProvider {

    /**
     * Restituisce la data locale corrente del dispositivo.
     *
     * @return data locale corrente.
     */
    override fun today(): LocalDate = LocalDate.now()
}
