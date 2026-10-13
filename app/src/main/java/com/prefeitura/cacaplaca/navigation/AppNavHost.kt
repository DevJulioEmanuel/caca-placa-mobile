package com.prefeitura.cacaplaca.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prefeitura.cacaplaca.feature.home.ui.HomeScreen
import com.prefeitura.cacaplaca.feature.map.Map


@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Home,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
        popEnterTransition = { EnterTransition.None },
        popExitTransition = { ExitTransition.None },
    ) {
        composable<Screen.Home> {
            HomeScreen(
                onViewPlatesClicked = {
                    navController.navigate(Screen.Map)
                },
                onRegisterPlateClicked = {
                    navController.navigate(Screen.Map)
                },
                onUpdatePlateClicked = {
                    navController.navigate(Screen.Map)
                },
                onProfileClicked = {
                    //navController.navigate(Screen.Profile)
                },
            )
        }

        composable<Screen.Map> {
            Map(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Registration> {
            Map(
                onBackClicked = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Profile> {
            //TODO
        }
    }
}
