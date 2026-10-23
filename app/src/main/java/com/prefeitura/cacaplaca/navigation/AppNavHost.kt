package com.prefeitura.cacaplaca.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prefeitura.cacaplaca.feature.auth.ui.LoginScreen
import com.prefeitura.cacaplaca.feature.auth.ui.RecoverPasswordScreen
import com.prefeitura.cacaplaca.feature.home.ui.HomeScreen
import com.prefeitura.cacaplaca.feature.map.Map

import androidx.lifecycle.Lifecycle
import com.prefeitura.cacaplaca.feature.auth.ui.EmailSentScreen
import com.prefeitura.cacaplaca.feature.registration.ui.screens.RegistrationScreen

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
                modifier = Modifier.safeDrawingPadding(),
                onViewPlatesClicked = {
                    navController.navigate(Screen.Map)
                },
                onRegisterPlateClicked = {
                    navController.navigate(Screen.Registration)
                },
                onUpdatePlateClicked = {
                    navController.navigate(Screen.Map)
                },
                onProfileClicked = {
                    navController.navigate(Screen.Profile)
                },
            )
        }

        composable<Screen.Map> {
            Map(
                modifier = Modifier.safeDrawingPadding(),
                onBackClicked = {
                    navController.safePopBackStack()
                }
            )
        }

        composable<Screen.Registration> {
            RegistrationScreen(
                modifier = Modifier.safeDrawingPadding(),
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
           // TODO
        }
    }
}