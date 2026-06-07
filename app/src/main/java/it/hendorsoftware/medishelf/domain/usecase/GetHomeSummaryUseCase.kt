package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.model.MedicineStatus
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import it.hendorsoftware.medishelf.domain.repository.UserSettingsRepository
import it.hendorsoftware.medishelf.domain.rules.MedicineStatusCalculator
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.Flow

/**
 * Calcola il riepilogo della Home a partire dall'inventario attivo.
 *
 * @param medicineRepository repository domain dei medicinali.
 * @param statusCalculator regola di dominio per lo stato derivato.
 *
 * Il use case evita che la dashboard replichi logiche di conteggio o priorita
 * nelle Composable, mantenendo coerenti Home, Inventario e Scadenzario.
 */
class GetHomeSummaryUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val statusCalculator: MedicineStatusCalculator,
) {

    /**
     * Osserva il riepilogo della Home.
     *
     * @return stream aggiornato dei conteggi e delle voci da tenere d'occhio.
     */
    operator fun invoke(): Flow<HomeSummary> =
        combine(
            medicineRepository.observeActiveMedicines(),
            userSettingsRepository.observeSettings(),
        ) { medicines, settings ->
            medicines.toHomeSummary(settings.expiringThresholdDays)
        }

    private fun List<Medicine>.toHomeSummary(expiringThresholdDays: Int): HomeSummary {
        val medicinesWithStatus = map { medicine ->
            medicine to statusCalculator.calculate(
                medicine = medicine,
                expiringThresholdDays = expiringThresholdDays,
            )
        }

        return HomeSummary(
            activeMedicineCount = size,
            expiringMedicineCount = medicinesWithStatus.countStatus(MedicineStatus.EXPIRING),
            expiredMedicineCount = medicinesWithStatus.countStatus(MedicineStatus.EXPIRED),
            lowStockMedicineCount = count { medicine -> medicine.isLowStockOrOutOfStock() },
            attentionMedicines = medicinesWithStatus
                .filter { (medicine, status) ->
                    status in AttentionStatuses || medicine.isLowStockOrOutOfStock()
                }
                .sortedWith(attentionComparator())
                .take(ATTENTION_ITEM_LIMIT)
                .map { (medicine, status) ->
                    HomeAttentionMedicine(
                        medicine = medicine,
                        status = status,
                        isLowStock = medicine.isLowStockOrOutOfStock(),
                    )
                },
        )
    }

    private fun List<Pair<Medicine, MedicineStatus>>.countStatus(status: MedicineStatus): Int =
        count { (_, currentStatus) -> currentStatus == status }

    private fun Medicine.isLowStockOrOutOfStock(): Boolean {
        val quantity = quantity ?: return false
        val lowStockThreshold = quantity.lowStockThreshold

        return quantity.amount <= OUT_OF_STOCK_LIMIT ||
            (lowStockThreshold != null && quantity.amount <= lowStockThreshold)
    }

    private fun attentionComparator(): Comparator<Pair<Medicine, MedicineStatus>> =
        compareBy<Pair<Medicine, MedicineStatus>> { (_, status) ->
            status.attentionPriority()
        }.thenBy { (medicine, _) ->
            medicine.expirationDate
        }.thenBy { (medicine, _) ->
            medicine.name.lowercase()
        }

    private fun MedicineStatus.attentionPriority(): Int = when (this) {
        MedicineStatus.EXPIRED -> 0
        MedicineStatus.OUT_OF_STOCK -> 1
        MedicineStatus.EXPIRING -> 2
        MedicineStatus.NO_EXPIRATION_DATE -> 3
        MedicineStatus.VALID -> 4
    }

    private companion object {
        private const val ATTENTION_ITEM_LIMIT = 3
        private const val OUT_OF_STOCK_LIMIT = 0.0
        private val AttentionStatuses = setOf(
            MedicineStatus.EXPIRED,
            MedicineStatus.OUT_OF_STOCK,
            MedicineStatus.EXPIRING,
            MedicineStatus.NO_EXPIRATION_DATE,
        )
    }
}
