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
import com.prefeitura.cacaplaca.core.session.SessionManager
import com.prefeitura.cacaplaca.feature.auth.ui.LoginScreen
import com.prefeitura.cacaplaca.feature.auth.ui.NewPasswordScreen
import com.prefeitura.cacaplaca.feature.auth.ui.PasswordChangedScreen
import com.prefeitura.cacaplaca.feature.auth.ui.RecoverPasswordScreen
import com.prefeitura.cacaplaca.feature.home.ui.HomeScreen
import com.prefeitura.cacaplaca.feature.map.Map

import androidx.lifecycle.Lifecycle

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
                onSendInstructions = {
                    navController.navigate(Screen.NewPassword)
                }
            )
        }

        composable<Screen.NewPassword> {
            NewPasswordScreen(
                onBack = { navController.safePopBackStack() },
                onNavigateToLogin = {
                    navController.popBackStack<Screen.Auth>(inclusive = false)
                },
                onSavePassword = {
                    navController.navigate(Screen.PasswordChanged) {
                        popUpTo<Screen.RecoverPassword> { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.PasswordChanged> {
            PasswordChangedScreen(
                onReturnToLogin = {
                    navController.navigate(Screen.Auth) {
                        popUpTo(0) { inclusive = true }
                    }
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
            Map(
                modifier = Modifier.safeDrawingPadding(),
                onBackClicked = {
                    navController.safePopBackStack()
                }
            )
        }

        composable<Screen.Profile> {
           // TODO
        }
    }
}