package com.prefeitura.cacaplaca.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prefeitura.cacaplaca.feature.auth.ui.LoginScreen
import com.prefeitura.cacaplaca.feature.auth.ui.NewPasswordScreen
import com.prefeitura.cacaplaca.feature.auth.ui.PasswordChangedScreen
import com.prefeitura.cacaplaca.feature.auth.ui.RecoverPasswordScreen
import com.prefeitura.cacaplaca.feature.map.Map

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onNavigateToRecover = { navController.navigate("recover_password") },
                onLoginSuccess = { 
                    navController.navigate("map") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        
        composable("recover_password") {
            RecoverPasswordScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = { 
                    navController.popBackStack("login", inclusive = false) 
                },
                onSendInstructions = { navController.navigate("new_password") }
            )
        }

        composable("new_password") {
            NewPasswordScreen(
                onBack = { navController.popBackStack() },
                onNavigateToLogin = { 
                    navController.popBackStack("login", inclusive = false) 
                },
                onSavePassword = { 
                    navController.navigate("password_changed") {
                        popUpTo("recover_password") { inclusive = true }
                    }
                }
            )
        }

        composable("password_changed") {
            PasswordChangedScreen(
                onReturnToLogin = { 
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable("map") {
            Map()
        }
    }
}