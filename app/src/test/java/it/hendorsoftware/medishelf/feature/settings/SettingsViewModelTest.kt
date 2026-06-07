package it.hendorsoftware.medishelf.feature.settings

import it.hendorsoftware.medishelf.domain.model.UserSettings
import it.hendorsoftware.medishelf.domain.repository.FakeMedicineRepository
import it.hendorsoftware.medishelf.domain.repository.FakeUserSettingsRepository
import it.hendorsoftware.medishelf.domain.usecase.ClearArchiveUseCase
import it.hendorsoftware.medishelf.domain.usecase.ObserveUserSettingsUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateExpiringThresholdDaysUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateNotificationsEnabledUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdatePreferArchiveOverDeleteUseCase
import it.hendorsoftware.medishelf.domain.usecase.UpdateThemeModeUseCase
import it.hendorsoftware.medishelf.testing.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * Test unitari per il ViewModel delle Impostazioni.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    /**
     * Verifica che la soglia digitata venga validata e salvata nel repository.
     */
    @Test
    fun shouldUpdateThreshold() = runTest {
        val settingsRepository = FakeUserSettingsRepository(
            initialSettings = UserSettings(expiringThresholdDays = 30),
        )
        val viewModel = createViewModel(settingsRepository = settingsRepository)

        advanceUntilIdle()
        viewModel.onThresholdClick()
        viewModel.onThresholdInputChanged("60")
        viewModel.onThresholdSaveClick()
        advanceUntilIdle()

        assertEquals(listOf(60), settingsRepository.updatedThresholds)
        assertEquals(60, viewModel.uiState.value.expiringThresholdDays)
        assertFalse(viewModel.uiState.value.isThresholdDialogVisible)
    }

    /**
     * Verifica che una soglia non numerica mantenga aperto il dialog con errore.
     */
    @Test
    fun shouldRejectInvalidThresholdInput() = runTest {
        val viewModel = createViewModel()

        advanceUntilIdle()
        viewModel.onThresholdClick()
        viewModel.onThresholdInputChanged("abc")
        viewModel.onThresholdSaveClick()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isThresholdDialogVisible)
        assertEquals(
            SettingsThresholdInputError.INVALID_NUMBER,
            viewModel.uiState.value.thresholdInputError,
        )
    }

    /**
     * Verifica che il toggle notifiche aggiorni la preferenza persistita.
     */
    @Test
    fun shouldToggleNotifications() = runTest {
        val settingsRepository = FakeUserSettingsRepository(
            initialSettings = UserSettings(notificationsEnabled = true),
        )
        val viewModel = createViewModel(settingsRepository = settingsRepository)

        advanceUntilIdle()
        viewModel.onNotificationsEnabledChanged(false)
        advanceUntilIdle()

        assertEquals(listOf(false), settingsRepository.updatedNotificationValues)
        assertFalse(viewModel.uiState.value.notificationsEnabled)
    }

    private fun createViewModel(
        settingsRepository: FakeUserSettingsRepository = FakeUserSettingsRepository(),
        medicineRepository: FakeMedicineRepository = FakeMedicineRepository(),
    ): SettingsViewModel =
        SettingsViewModel(
            observeUserSettingsUseCase = ObserveUserSettingsUseCase(settingsRepository),
            updateThemeModeUseCase = UpdateThemeModeUseCase(settingsRepository),
            updateExpiringThresholdDaysUseCase = UpdateExpiringThresholdDaysUseCase(settingsRepository),
            updateNotificationsEnabledUseCase = UpdateNotificationsEnabledUseCase(settingsRepository),
            updatePreferArchiveOverDeleteUseCase = UpdatePreferArchiveOverDeleteUseCase(settingsRepository),
            clearArchiveUseCase = ClearArchiveUseCase(medicineRepository),
        )
}
