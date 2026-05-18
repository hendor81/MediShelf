package it.hendorsoftware.medishelf.domain.usecase

import it.hendorsoftware.medishelf.domain.model.Medicine
import it.hendorsoftware.medishelf.domain.repository.MedicineRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/**
 * Recupera lo stream dei medicinali presenti nell'inventario attivo.
 *
 * Il use case offre ai ViewModel un punto stabile da osservare, senza esporre
 * DAO o repository concreti fuori dal dominio.
 */
class GetActiveMedicinesUseCase @Inject constructor(
    private val medicineRepository: MedicineRepository,
) {

    /**
     * Osserva i medicinali non archiviati.
     *
     * @return stream dei medicinali attivi fornito dal repository domain.
     */
    operator fun invoke(): Flow<List<Medicine>> = medicineRepository.observeActiveMedicines()
}
