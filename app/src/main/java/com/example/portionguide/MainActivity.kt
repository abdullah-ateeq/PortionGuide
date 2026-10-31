package com.example.portionguide

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.portionguide.ui.navigation.Screen
import com.example.portionguide.ui.screens.AboutScreen
import com.example.portionguide.ui.screens.CalculatorScreen
import com.example.portionguide.ui.screens.HistoryScreen
import com.example.portionguide.ui.screens.HomeScreen
import com.example.portionguide.ui.screens.ResultScreen
import com.example.portionguide.ui.screens.SettingsScreen
import com.example.portionguide.ui.screens.SplashScreen
import com.example.portionguide.ui.theme.PortionGuideTheme
import com.example.portionguide.viewmodel.PortionViewModel
import com.example.portionguide.viewmodel.PortionViewModelFactory

class MainActivity : ComponentActivity() {

    private val viewModel: PortionViewModel by viewModels {
        val app = application as PortionGuideApplication
        PortionViewModelFactory(
            repository = app.repository,
            preferencesRepository = app.preferencesRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val preferences by viewModel.preferences.collectAsState()

            PortionGuideTheme(themeMode = preferences.themeMode) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PortionGuideNavigation(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun PortionGuideNavigation(viewModel: PortionViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCalculator = {
                    navController.navigate(Screen.Calculator.route)
                },
                onNavigateToResult = {
                    navController.navigate(Screen.Result.route)
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.History.route)
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.Calculator.route) {
            CalculatorScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onCalculationSuccess = {
                    navController.navigate(Screen.Result.route)
                }
            )
        }

        composable(Screen.Result.route) {
            ResultScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onEditInputs = {
                    navController.popBackStack(Screen.Calculator.route, inclusive = false)
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenResult = {
                    navController.navigate(Screen.Result.route)
                },
                onStartCalculation = {
                    navController.navigate(Screen.Calculator.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToAbout = {
                    navController.navigate(Screen.About.route)
                }
            )
        }

        composable(Screen.About.route) {
            AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
