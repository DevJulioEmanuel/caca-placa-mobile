package com.prefeitura.cacaplaca.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.LaunchedEffect
import com.prefeitura.cacaplaca.feature.auth.ui.LoginScreen
import com.prefeitura.cacaplaca.feature.auth.ui.RecoverPasswordScreen
import com.prefeitura.cacaplaca.feature.home.ui.HomeScreen
import com.prefeitura.cacaplaca.feature.map.ui.screens.MapScreen

import androidx.lifecycle.Lifecycle
import com.prefeitura.cacaplaca.feature.auth.ui.EmailSentScreen
import com.prefeitura.cacaplaca.feature.registration.ui.screens.RegistrationScreen
import com.prefeitura.cacaplaca.core.session.SessionManager
import org.koin.compose.koinInject
import androidx.compose.material3.Scaffold
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue

fun NavHostController.safePopBackStack() {
    val currentEntry = this.currentBackStackEntry
    if (currentEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED &&
        this.previousBackStackEntry != null
    ) {
        this.popBackStack()
    }
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: Screen = Screen.Auth,
    sessionManager: SessionManager = koinInject()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    LaunchedEffect(Unit) {
        sessionManager.sessionExpiredFlow.collect {
            navController.navigate(Screen.Auth) {
                popUpTo(0)
            }
        }
    }

    val showBottomBar = currentDestination?.hasRoute<Screen.Home>() == true ||
            currentDestination?.hasRoute<Screen.Map>() == true ||
            currentDestination?.hasRoute<Screen.Profile>() == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                CacaPlacaBottomBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(innerPadding),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
        ) {
        composable<Screen.Auth> {
            LoginScreen(
                onNavigateToRecover = {
                    navController.navigate(Screen.RecoverPassword)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo<Screen.Auth> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.RecoverPassword> {
            RecoverPasswordScreen(
                onBack = { navController.safePopBackStack() },
                onNavigateToLogin = {
                    navController.popBackStack<Screen.Auth>(inclusive = false)
                },
                onNavigateToEmailSent = {
                    navController.navigate(Screen.EmailSent) {
                        popUpTo<Screen.RecoverPassword> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.EmailSent> {
            EmailSentScreen(
                onReturnToLogin = {
                    navController.popBackStack<Screen.Auth>(inclusive = false)
                }
            )
        }

        composable<Screen.Home> {
            HomeScreen(
                modifier = Modifier,
                onViewPlatesClicked = {
                    navController.navigate(Screen.Map) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onRegisterPlateClicked = {
                    navController.navigate(Screen.Registration)
                },
                onUpdatePlateClicked = {
                    navController.navigate(Screen.Map) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onProfileClicked = {
                    navController.navigate(Screen.Profile) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
            )
        }

        composable<Screen.Map> {
            MapScreen(
                modifier = Modifier,
                onBackClicked = {
                    navController.safePopBackStack()
                },
                onProfileClicked = {
                    navController.navigate(Screen.Profile) {
                        popUpTo(Screen.Home) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable<Screen.Registration> {
            RegistrationScreen(
                modifier = Modifier,
                onBackToHome = {
                    navController.safePopBackStack()
                },
                onNavigateToMap = {
                    navController.navigate(Screen.Map) {
                        popUpTo<Screen.Registration> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.Profile> {
            com.prefeitura.cacaplaca.feature.profile.ui.screens.ProfileScreen(
                modifier = Modifier,
                onNavigateBack = { navController.safePopBackStack() },
                onLogoutClicked = {
                    sessionManager.clearSession()
                    navController.navigate(Screen.Auth) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
    }
}