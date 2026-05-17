package it.hendorsoftware.medishelf.core.time

import java.time.LocalDate

/**
 * Fornisce la data locale corrente alle regole di dominio basate sul tempo.
 *
 * L'interfaccia permette di sostituire la data reale con una data controllata
 * nei test, evitando che il calcolo degli stati dipenda dall'orologio di sistema.
 */
interface DateProvider {

    /**
     * Restituisce la data locale da usare come riferimento per il calcolo.
     *
     * @return data corrente locale secondo l'implementazione concreta.
     */
    fun today(): LocalDate
}
