package it.hendorsoftware.medishelf

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfDimens
import it.hendorsoftware.medishelf.core.designsystem.theme.MediShelfTheme
import it.hendorsoftware.medishelf.navigation.MediShelfNavHost
import it.hendorsoftware.medishelf.navigation.MediShelfRoute

/**
 * Root composable dell'app MediShelf.
 *
 * Ospita il grafo Navigation Compose principale della versione Free e la
 * navigation bar delle destinazioni primarie.
 */
@Composable
fun MediShelfApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination.shouldShowBottomNavigation()) {
                MediShelfBottomNavigationBar(
                    selectedRoute = currentDestination.selectedBottomNavigationRoute(),
                    onDestinationClick = { destination ->
                        navController.navigateToBottomNavigationDestination(
                            destination = destination,
                            selectedRoute = currentDestination.selectedBottomNavigationRoute(),
                        )
                    },
                )
            }
        },
    ) { innerPadding ->
        MediShelfNavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
        )
    }
}

private fun NavHostController.navigateToBottomNavigationDestination(
    destination: BottomNavigationDestination,
    selectedRoute: MediShelfRoute?,
) {
    if (selectedRoute == destination.route) {
        return
    }

    if (destination.route == MediShelfRoute.Home) {
        val returnedToHome = popBackStack(
            route = MediShelfRoute.Home.route,
            inclusive = false,
        )
        if (!returnedToHome) {
            navigate(MediShelfRoute.Home.route) {
                launchSingleTop = true
            }
        }
        return
    }

    navigate(destination.route.route) {
        popUpTo(MediShelfRoute.Home.route) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun MediShelfBottomNavigationBar(
    selectedRoute: MediShelfRoute?,
    onDestinationClick: (BottomNavigationDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
    ) {
        BottomNavigationDestination.entries.forEach { destination ->
            NavigationBarItem(
                selected = selectedRoute == destination.route,
                onClick = { onDestinationClick(destination) },
                icon = {
                    Image(
                        painter = painterResource(destination.iconResId),
                        contentDescription = stringResource(destination.contentDescriptionResId),
                        modifier = Modifier.size(MediShelfDimens.BottomNavigationIconSize),
                    )
                },
                label = {
                    Text(text = stringResource(destination.labelResId))
                },
            )
        }
    }
}

private fun NavDestination?.shouldShowBottomNavigation(): Boolean =
    selectedBottomNavigationRoute() != null

private fun NavDestination?.selectedBottomNavigationRoute(): MediShelfRoute? =
    BottomNavigationDestination.entries.any { destination ->
        isRouteSelected(destination.route)
    }.let { hasBottomDestination ->
        if (hasBottomDestination) {
            BottomNavigationDestination.entries.first { destination ->
                isRouteSelected(destination.route)
            }.route
        } else {
            null
        }
    }

private fun NavDestination?.isRouteSelected(route: MediShelfRoute): Boolean =
    this?.hierarchy?.any { destination -> destination.route == route.route } == true

private enum class BottomNavigationDestination(
    val route: MediShelfRoute,
    val iconResId: Int,
    val labelResId: Int,
    val contentDescriptionResId: Int,
) {
    Home(
        route = MediShelfRoute.Home,
        iconResId = R.drawable.home,
        labelResId = R.string.bottom_navigation_home,
        contentDescriptionResId = R.string.bottom_navigation_home_content_description,
    ),
    Inventory(
        route = MediShelfRoute.Inventory,
        iconResId = R.drawable.inventario,
        labelResId = R.string.bottom_navigation_inventory,
        contentDescriptionResId = R.string.bottom_navigation_inventory_content_description,
    ),
    Expiry(
        route = MediShelfRoute.Expiry,
        iconResId = R.drawable.scadenzario,
        labelResId = R.string.bottom_navigation_expiry,
        contentDescriptionResId = R.string.bottom_navigation_expiry_content_description,
    ),
    Settings(
        route = MediShelfRoute.Settings,
        iconResId = R.drawable.impostazioni,
        labelResId = R.string.bottom_navigation_settings,
        contentDescriptionResId = R.string.bottom_navigation_settings_content_description,
    ),
}

/**
 * Preview dello scaffold iniziale MediShelf.
 */
@Preview(showBackground = true)
@Composable
private fun MediShelfAppPreview() {
    MediShelfTheme {
        MediShelfApp()
    }
}

/**
 * Preview della bottom navigation con Home selezionata.
 */
@Preview(showBackground = true)
@Composable
private fun MediShelfBottomNavigationBarPreview() {
    MediShelfTheme {
        MediShelfBottomNavigationBar(
            selectedRoute = MediShelfRoute.Home,
            onDestinationClick = {},
        )
    }
}
