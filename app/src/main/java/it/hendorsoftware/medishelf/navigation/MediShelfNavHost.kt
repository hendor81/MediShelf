package it.hendorsoftware.medishelf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.feature.archive.ArchiveRoute
import it.hendorsoftware.medishelf.feature.expiry.ExpiryRoute
import it.hendorsoftware.medishelf.feature.home.HomeRoute
import it.hendorsoftware.medishelf.feature.home.HomeScreen
import it.hendorsoftware.medishelf.feature.home.HomeUiState
import it.hendorsoftware.medishelf.feature.inventory.InventoryRoute
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailRoute
import it.hendorsoftware.medishelf.feature.medicineform.MedicineFormRoute
import it.hendorsoftware.medishelf.feature.settings.SettingsScreen

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
                onAttentionItemClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
            )
        }
        composable(MediShelfRoute.Inventory.route) {
            InventoryRoute(
                onAddMedicineClick = { navController.navigate(MediShelfRoute.AddMedicine.route) },
                onMedicineClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
                onArchiveClick = { navController.navigate(MediShelfRoute.Archive.route) },
            )
        }
        composable(MediShelfRoute.Expiry.route) {
            ExpiryRoute(
                onMedicineClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
            )
        }
        composable(MediShelfRoute.Archive.route) {
            ArchiveRoute(
                onMedicineClick = { medicineId ->
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(medicineId))
                },
            )
        }
        composable(MediShelfRoute.Settings.route) {
            SettingsScreen()
        }
        composable(MediShelfRoute.AddMedicine.route) {
            MedicineFormRoute(
                onSaved = {
                    navController.navigate(MediShelfRoute.Inventory.route) {
                        popUpTo(MediShelfRoute.Home.route)
                    }
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
                onArchived = { navController.navigate(MediShelfRoute.Archive.route) },
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
                onSaved = { navController.popBackStack() },
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
