package it.hendorsoftware.medishelf.domain.rules

import it.hendorsoftware.medishelf.core.time.DateProvider
import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import javax.inject.Inject

/**
 * Calcola lo stato derivato di un medicinale in base a quantita e scadenza.
 *
 * @param dateProvider provider testabile della data corrente.
 *
 * L'algoritmo applica la priorita prevista per la versione Free: esaurito,
 * scadenza assente, scaduto, in scadenza, valido. Lo stato non viene persistito
 * e resta una regola di dominio calcolata al bisogno.
 */
class MedicineStatusCalculator @Inject constructor(
    private val dateProvider: DateProvider,
) {

    /**
     * Calcola lo stato corrente di [medicine].
     *
     * @param medicine medicinale da classificare.
     * @param expiringThresholdDays numero di giorni entro cui una scadenza e vicina.
     * @return stato derivato dal dominio.
     *
     * @throws IllegalArgumentException se [expiringThresholdDays] e negativo.
     */
    fun calculate(
        medicine: Medicine,
        expiringThresholdDays: Int,
    ): MedicineStatus {
        require(expiringThresholdDays >= 0) {
            "Expiring threshold days cannot be negative."
        }

        if (medicine.quantity != null && medicine.quantity.amount <= 0.0) {
            return MedicineStatus.OUT_OF_STOCK
        }

        val expirationDate = medicine.expirationDate
            ?: return MedicineStatus.NO_EXPIRATION_DATE

        val today = dateProvider.today()

        if (expirationDate.isBefore(today)) {
            return MedicineStatus.EXPIRED
        }

        val expiringLimit = today.plusDays(expiringThresholdDays.toLong())

        // La soglia e inclusiva: una scadenza esattamente al limite e in scadenza.
        return if (!expirationDate.isAfter(expiringLimit)) {
            MedicineStatus.EXPIRING
        } else {
            MedicineStatus.VALID
        }
    }
}
