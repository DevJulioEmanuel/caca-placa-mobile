package com.prefeitura.cacaplaca.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaNavy
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaOrange
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTextSecondary

data class BottomNavItem(
    val title: String,
    val icon: ImageVector,
    val route: Screen
)

@Composable
fun CacaPlacaBottomBar(
    navController: NavHostController,
    currentDestination: NavDestination?
) {
    val items = listOf(
        BottomNavItem(
            title = "Início",
            icon = Icons.Outlined.Home,
            route = Screen.Home
        ),
        BottomNavItem(
            title = "Mapa",
            icon = Icons.Outlined.Map,
            route = Screen.Map
        ),
        BottomNavItem(
            title = "Perfil",
            icon = Icons.Outlined.Person,
            route = Screen.Profile
        )
    )

    NavigationBar(
        containerColor = CacaPlacaNavy,
        contentColor = Color.White
    ) {
        items.forEach { item ->
            // In navigation-compose 2.8.0+, we check if the current destination has the given route class
            val isSelected = currentDestination?.hasRoute(item.route::class) == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Screen.Home) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CacaPlacaOrange,
                    selectedTextColor = CacaPlacaOrange,
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = CacaPlacaTextSecondary,
                    unselectedTextColor = CacaPlacaTextSecondary
                )
            )
        }
    }
}
