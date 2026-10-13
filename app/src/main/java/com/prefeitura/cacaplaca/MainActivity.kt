package com.prefeitura.cacaplaca

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.prefeitura.cacaplaca.feature.home.ui.HomeScreen
import com.prefeitura.cacaplaca.feature.map.Map
import com.prefeitura.cacaplaca.ui.theme.CacaPlacaTheme

object Routes {
    const val HOME = "home"
    const val MAP = "map"
    const val REGISTRATION = "registration"
    const val PROFILE = "profile"
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CacaPlacaTheme {
                val navController = rememberNavController()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .safeDrawingPadding(),
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HOME,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        composable(Routes.HOME) {
                            HomeScreen(
                                onViewPlatesClicked = {
                                    navController.navigate(Routes.MAP)
                                },
                                onRegisterPlateClicked = {
                                    navController.navigate(Routes.MAP)
                                },
                                onUpdatePlateClicked = {
                                    navController.navigate(Routes.MAP)
                                },
                                onProfileClicked = {
                                    navController.navigate(Routes.PROFILE)
                                },
                            )
                        }

                        composable(Routes.MAP) {
                            Map(
                                onBackClicked = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Routes.REGISTRATION) {
                            Map(
                                onBackClicked = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(Routes.PROFILE) {
                            Map(
                                onBackClicked = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
