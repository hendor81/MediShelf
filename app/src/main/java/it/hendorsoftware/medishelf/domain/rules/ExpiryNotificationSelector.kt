package it.hendorsoftware.medishelf.domain.rules

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import it.hendorsoftware.medishelf.domain.model.UserSettings
import javax.inject.Inject

/** Seleziona solo scadenze organizzative, applicando preferenze e priorità del dominio. */
class ExpiryNotificationSelector @Inject constructor(
    private val calculator: MedicineStatusCalculator,
) {
    /** Restituisce le voci notificabili; quantità assente ammessa, archiviate ed esaurite escluse. */
    fun select(medicines: List<Medicine>, settings: UserSettings): List<Medicine> {
        if (!settings.notificationsEnabled) return emptyList()
        return medicines.filter { medicine ->
            !medicine.isArchived && calculator.calculate(medicine, settings.expiringThresholdDays) in
                setOf(MedicineStatus.EXPIRING, MedicineStatus.EXPIRED)
        }
    }
}
