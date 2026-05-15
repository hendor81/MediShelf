package it.hendorsoftware.medishelf.navigation

/**
 * Route principali della versione Free di MediShelf.
 *
 * Ogni route espone una stringa stabile per Navigation Compose. Le schermate
 * con argomenti ricevono solo identificativi minimi, senza serializzare oggetti
 * complessi tra destinazioni.
 *
 * @param route pattern usato dal grafo di navigazione.
 */
sealed class MediShelfRoute(val route: String) {

    data object Home : MediShelfRoute("home")
    data object Inventory : MediShelfRoute("inventory")
    data object Expiry : MediShelfRoute("expiry")
    data object Archive : MediShelfRoute("archive")
    data object Settings : MediShelfRoute("settings")
    data object AddMedicine : MediShelfRoute("medicine/add")

    data object MedicineDetail : MediShelfRoute("medicine/{$MEDICINE_ID_ARGUMENT}") {
        /**
         * Crea la route concreta per aprire il dettaglio di un medicinale.
         *
         * @param medicineId identificativo minimo del medicinale da mostrare.
         * @return route navigabile con parametro valorizzato.
         */
        fun createRoute(medicineId: String): String = "medicine/$medicineId"
    }

    data object EditMedicine : MediShelfRoute("medicine/{$MEDICINE_ID_ARGUMENT}/edit") {
        /**
         * Crea la route concreta per aprire la modifica di un medicinale.
         *
         * @param medicineId identificativo minimo del medicinale da modificare.
         * @return route navigabile con parametro valorizzato.
         */
        fun createRoute(medicineId: String): String = "medicine/$medicineId/edit"
    }

    companion object {
        const val MEDICINE_ID_ARGUMENT = "medicineId"
    }
}
