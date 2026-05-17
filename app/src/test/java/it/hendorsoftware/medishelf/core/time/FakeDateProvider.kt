package it.hendorsoftware.medishelf.core.time

import java.time.LocalDate

/**
 * Provider di data controllata per test unitari deterministici.
 *
 * @param fixedToday data restituita da [today].
 */
class FakeDateProvider(
    private val fixedToday: LocalDate,
) : DateProvider {

    /**
     * Restituisce sempre la data fissata dal test.
     *
     * @return data controllata.
     */
    override fun today(): LocalDate = fixedToday
}
