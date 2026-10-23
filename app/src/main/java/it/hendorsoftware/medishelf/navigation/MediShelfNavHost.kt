package it.hendorsoftware.medishelf.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.hendorsoftware.medishelf.R
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.feature.archive.ArchiveRoute
import it.hendorsoftware.medishelf.feature.expiry.ExpiryRoute
import it.hendorsoftware.medishelf.feature.home.HomeRoute
import it.hendorsoftware.medishelf.feature.home.HomeScreen
import it.hendorsoftware.medishelf.feature.home.HomeUiState
import it.hendorsoftware.medishelf.feature.inventory.InventoryRoute
import it.hendorsoftware.medishelf.feature.inventory.InventoryStatusFilter
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailRoute
import it.hendorsoftware.medishelf.feature.medicineform.MedicineFormRoute
import it.hendorsoftware.medishelf.feature.settings.SettingsRoute
import kotlinx.coroutines.launch

/**
 * NavHost principale della navigation shell MediShelf.
 *
 * @param modifier modificatore Compose applicato al contenitore del grafo.
 * @param navController controller di navigazione, iniettabile dai test UI.
 */
@Composable
fun MediShelfNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    // Il controllo LocalInspectionMode.current permette di evitare il crash di hiltViewModel()
    // durante le preview di Android Studio, dove il contesto non è un'Activity Hilt.
    if (LocalInspectionMode.current) {
        HomeScreen(
            uiState = HomeUiState(),
            onAddMedicineClick = {},
            onInventoryClick = {},
            onExpiryClick = {},
            onAttentionItemClick = {},
            modifier = modifier
        )
        return
    }

    val scope = rememberCoroutineScope()
    val savedMessage = stringResource(R.string.medicine_form_save_success)
    val updatedMessage = stringResource(R.string.medicine_form_update_success)
    NavHost(
        navController = navController,
        startDestination = MediShelfRoute.Home.route,
        modifier = modifier,
    ) {
        composable(MediShelfRoute.Home.route) {
            HomeRoute(
                onAddMedicineClick = { navController.navigate(MediShelfRoute.AddMedicine.route) },
                onInventoryClick = { navController.navigate(MediShelfRoute.Inventory.route) },
                onExpiryClick = { navController.navigate(MediShelfRoute.Expiry.route) },
                onSeeAllAttentionClick = { navController.navigate("inventory?filter=attention") },
                onAttentionItemClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
            )
        }
        composable(
            "inventory?filter={filter}",
            arguments = listOf(navArgument("filter") { type = NavType.StringType; defaultValue = "all" }),
        ) { entry ->
            InventoryRoute(
                initialFilter = if (entry.arguments?.getString("filter") == "attention") InventoryStatusFilter.Attention else InventoryStatusFilter.All,
                onAddMedicineClick = { navController.navigate(MediShelfRoute.AddMedicine.route) },
                onMedicineClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
                onArchiveClick = { navController.navigate(MediShelfRoute.Archive.route) },
            )
        }
        composable(MediShelfRoute.Expiry.route) {
            ExpiryRoute(
                onBackClick = { navController.popBackStack() },
                onAddMedicineClick = { navController.navigate(MediShelfRoute.AddMedicine.route) },
                onArchiveClick = { navController.navigate(MediShelfRoute.Archive.route) },
                onMedicineClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
            )
        }
        composable(MediShelfRoute.Archive.route) {
            ArchiveRoute(
                onBackClick = { navController.popBackStack() },
                onMedicineClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
            )
        }
        composable(MediShelfRoute.Settings.route) {
            SettingsRoute()
        }
        composable(MediShelfRoute.AddMedicine.route) {
            MedicineFormRoute(
                onCloseClick = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack()
                    scope.launch { snackbarHostState.showSnackbar(savedMessage) }
                },
            )
        }
        composable(
            route = MediShelfRoute.MedicineDetail.route,
            arguments = medicineIdArguments(),
        ) { backStackEntry ->
            val medicineId = backStackEntry.requireMedicineId()
            MedicineDetailRoute(
                medicineId = medicineId,
                onEditClick = {
                    navController.navigate(MediShelfRoute.EditMedicine.createRoute(medicineId))
                },
                onArchived = {
                    navController.navigate(MediShelfRoute.Archive.route) {
                        popUpTo(MediShelfRoute.MedicineDetail.route) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onDeleted = { navController.popBackStack() },
                onCloseClick = { navController.popBackStack() },
            )
        }
        composable(
            route = MediShelfRoute.EditMedicine.route,
            arguments = medicineIdArguments(),
        ) { backStackEntry ->
            MedicineFormRoute(
                medicineId = backStackEntry.requireMedicineId(),
                onSaved = {
                    navController.popBackStack()
                    scope.launch { snackbarHostState.showSnackbar(updatedMessage) }
                },
                onCloseClick = { navController.popBackStack() },
            )
        }
    }
}

/**
 * Argomenti condivisi dalle route che richiedono un identificativo medicinale.
 *
 * @return lista di argomenti Navigation Compose per `medicineId`.
 */
private fun medicineIdArguments() = listOf(
    navArgument(MediShelfRoute.MEDICINE_ID_ARGUMENT) {
        type = NavType.StringType
    },
)

/**
 * Estrae il parametro obbligatorio `medicineId` da una destinazione.
 *
 * @return identificativo presente nella route, oppure stringa vuota se il
 * parametro manca a causa di una navigazione non valida.
 */
private fun NavBackStackEntry.requireMedicineId(): String =
    arguments?.getString(MediShelfRoute.MEDICINE_ID_ARGUMENT).orEmpty()

/**
 * Preview del grafo di navigazione con Home come destinazione iniziale.
 */
@Preview(showBackground = true)
@Composable
private fun MediShelfNavHostPreview() {
    MediShelfTheme {
        MediShelfNavHost()
    }
}
