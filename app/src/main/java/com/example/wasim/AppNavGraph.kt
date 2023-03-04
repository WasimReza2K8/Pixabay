package com.example.wasim

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.example.core.navigation.register
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavGraph(
    navController: NavHostController,
    featureProvider: FeatureProvider,
) {
    AnimatedNavHost(
        navController = navController,
        startDestination = featureProvider.searchLauncher.route()
    ) {
        featureProvider.launchers.forEach { launcher ->
            register(launcher)
        }
    }
}
