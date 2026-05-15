package it.hendorsoftware.medishelf

import android.app.Application

/**
 * Punto di ingresso applicativo di MediShelf.
 *
 * Questa classe resta volutamente minimale nella issue di setup: prepara il
 * progetto a una futura configurazione Hilt senza introdurre ancora moduli o
 * dipendenze applicative non necessarie.
 */
class MediShelfApplication : Application()
