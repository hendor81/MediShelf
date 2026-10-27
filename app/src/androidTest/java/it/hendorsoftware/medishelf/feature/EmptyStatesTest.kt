package it.hendorsoftware.medishelf.feature

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.junit4.createComposeRule
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.feature.archive.ArchiveScreen
import it.hendorsoftware.medishelf.feature.archive.ArchiveUiState
import it.hendorsoftware.medishelf.feature.expiry.ExpiryTestTags
import it.hendorsoftware.medishelf.feature.expiry.ExpiryScreen
import it.hendorsoftware.medishelf.feature.expiry.ExpiryUiState
import it.hendorsoftware.medishelf.feature.home.HomeScreen
import it.hendorsoftware.medishelf.feature.home.HomeUiState
import it.hendorsoftware.medishelf.feature.home.HomeTestTags
import it.hendorsoftware.medishelf.feature.inventory.InventoryTestTags
import it.hendorsoftware.medishelf.feature.inventory.InventoryScreen
import it.hendorsoftware.medishelf.feature.inventory.InventoryUiState
import it.hendorsoftware.medishelf.feature.inventory.InventoryStatusFilter
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailScreen
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import it.hendorsoftware.medishelf.setItalianContent
import org.junit.Rule
import org.junit.Test

/** Verifica spiegazioni e azioni di recupero nelle schermate senza contenuti. */
class EmptyStatesTest {
    @get:Rule val compose = createComposeRule()

    /** La prima Home invita ad aggiungere senza dichiarare tutto in ordine. */
    @Test fun shouldExplainEmptyHomeAndAllowAdding() {
        var clicks = 0
        compose.setItalianContent {
            MediShelfTheme {
                HomeScreen(HomeUiState(isLoading = false), { clicks++ }, {}, {}, {})
            }
        }
        compose.onNodeWithText("Il tuo armadietto è ancora vuoto").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Tutto in ordine").assertDoesNotExist()
        compose.onNodeWithTag(HomeTestTags.AddMedicineButton).performClick()
        assertEquals(1, clicks)
    }

    /** Una Home popolata senza priorità mantiene la rassicurazione contestuale. */
    @Test fun shouldReassureWhenNoMedicineNeedsAttention() {
        compose.setItalianContent {
            MediShelfTheme {
                HomeScreen(HomeUiState(isLoading = false, activeMedicineCount = 1), {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("Tutto in ordine").performScrollTo().assertIsDisplayed()
        compose.onNodeWithText("Il tuo armadietto è ancora vuoto").assertDoesNotExist()
    }

    /** La CTA dell'inventario vuoto apre l'inserimento. */
    @Test fun shouldAddFromEmptyInventory() {
        var clicks = 0
        compose.setItalianContent {
            MediShelfTheme {
                InventoryScreen(InventoryUiState(isLoading = false), { clicks++ }, {}, {}, {}, {}, {})
            }
        }
        compose.onNodeWithText("Inventario vuoto").assertIsDisplayed()
        compose.onNode(hasText("Aggiungi medicinale") and hasAnyAncestor(hasTestTag(InventoryTestTags.EmptyState)))
            .performScrollTo().assertIsDisplayed().performClick()
        assertEquals(1, clicks)
    }

    /** Ricerca e filtro vengono entrambi rimossi senza creare nuove voci. */
    @Test fun shouldResetSearchAndStatusWhenNoResults() {
        var cleared = false
        var selected = InventoryStatusFilter.Expired
        compose.setItalianContent {
            MediShelfTheme {
                InventoryScreen(
                    InventoryUiState(isLoading = false, hasActiveMedicines = true,
                        searchQuery = "assente", selectedStatusFilter = InventoryStatusFilter.Expired),
                    {}, {}, {}, {}, { cleared = true }, { selected = it },
                )
            }
        }
        compose.onNodeWithText("Mostra tutti i medicinali").performScrollTo().assertIsDisplayed().performClick()
        assertTrue(cleared)
        assertEquals(InventoryStatusFilter.All, selected)
    }

    /** Lo Scadenzario vuoto spiega cosa conterrà e permette l'aggiunta. */
    @Test fun shouldAddFromEmptyExpiry() {
        var clicks = 0
        compose.setItalianContent {
            MediShelfTheme {
                ExpiryScreen(ExpiryUiState(isLoading = false), {}, onAddMedicineClick = { clicks++ })
            }
        }
        compose.onNodeWithText("Nessuna scadenza da gestire").assertIsDisplayed()
        compose.onNode(hasText("Aggiungi medicinale") and hasAnyAncestor(hasTestTag(ExpiryTestTags.EmptyState)))
            .performScrollTo().assertIsDisplayed().performClick()
        assertEquals(1, clicks)
    }

    /** L'Archivio vuoto permette di tornare al percorso di origine. */
    @Test fun shouldLeaveEmptyArchive() {
        var clicks = 0
        compose.setItalianContent {
            MediShelfTheme {
                ArchiveScreen(ArchiveUiState(isLoading = false), {}, { clicks++ })
            }
        }
        compose.onNodeWithText("Archivio vuoto").assertIsDisplayed()
        compose.onNodeWithText("Torna indietro").performClick()
        assertEquals(1, clicks)
    }

    /** Un errore di caricamento espone un retry esplicito e nessun falso non trovato. */
    @Test fun shouldRetryDetailLoadingError() {
        var clicks = 0
        compose.setItalianContent {
            MediShelfTheme {
                MedicineDetailScreen(
                    MedicineDetailUiState(isLoading = false, hasLoadError = true),
                    {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, onRetryClick = { clicks++ },
                )
            }
        }
        compose.onNodeWithText("Non riusciamo a caricare il medicinale").assertIsDisplayed()
        compose.onNodeWithText("Medicinale non trovato").assertDoesNotExist()
        compose.onNodeWithText("Riprova").performClick()
        assertEquals(1, clicks)
    }
}
