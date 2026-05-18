package it.hendorsoftware.medishelf.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.feature.archive.ArchiveScreen
import it.hendorsoftware.medishelf.feature.expiry.ExpiryScreen
import it.hendorsoftware.medishelf.feature.home.HomeScreen
import it.hendorsoftware.medishelf.feature.inventory.InventoryScreen
import it.hendorsoftware.medishelf.feature.medicinedetail.MedicineDetailScreen
import it.hendorsoftware.medishelf.feature.medicineform.MedicineFormRoute
import it.hendorsoftware.medishelf.feature.settings.SettingsScreen

private const val SAMPLE_MEDICINE_ID = "sample-medicine-id"

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
    NavHost(
        navController = navController,
        startDestination = MediShelfRoute.Home.route,
        modifier = modifier,
    ) {
        composable(MediShelfRoute.Home.route) {
            HomeScreen(
                onAddMedicineClick = { navController.navigate(MediShelfRoute.AddMedicine.route) },
                onInventoryClick = { navController.navigate(MediShelfRoute.Inventory.route) },
                onExpiryClick = { navController.navigate(MediShelfRoute.Expiry.route) },
                onSettingsClick = { navController.navigate(MediShelfRoute.Settings.route) },
            )
        }
        composable(MediShelfRoute.Inventory.route) {
            InventoryScreen(
                onAddMedicineClick = { navController.navigate(MediShelfRoute.AddMedicine.route) },
                onMedicineClick = {
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(SAMPLE_MEDICINE_ID))
                },
                onArchiveClick = { navController.navigate(MediShelfRoute.Archive.route) },
            )
        }
        composable(MediShelfRoute.Expiry.route) {
            ExpiryScreen(
                onMedicineClick = {
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(SAMPLE_MEDICINE_ID))
                },
            )
        }
        composable(MediShelfRoute.Archive.route) {
            ArchiveScreen(
                onMedicineClick = {
                    navController.navigate(MediShelfRoute.MedicineDetail.createRoute(SAMPLE_MEDICINE_ID))
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
            MedicineDetailScreen(
                medicineId = medicineId,
                onEditClick = {
                    navController.navigate(MediShelfRoute.EditMedicine.createRoute(medicineId))
                },
                onArchiveClick = { navController.navigate(MediShelfRoute.Archive.route) },
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
