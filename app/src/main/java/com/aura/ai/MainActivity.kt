package com.aura.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aura.ai.ui.navigation.NavDestination
import com.aura.ai.ui.screens.HomeScreen
import com.aura.ai.ui.theme.T1000Theme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            T1000Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    T1000Navigation()
                }
            }
        }
    }
}

@Composable
private fun T1000Navigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = NavDestination.HOME.route
    ) {
        composable(NavDestination.HOME.route) {
            HomeScreen()
        }
    }
}
