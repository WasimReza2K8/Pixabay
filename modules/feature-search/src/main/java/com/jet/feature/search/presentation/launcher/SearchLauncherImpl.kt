package com.jet.feature.search.presentation.launcher

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import com.example.core.ui.utils.ANIMATION_DURATION
import com.google.accompanist.navigation.animation.composable

import com.jet.feature.search.presentation.view.SearchScreen
import com.jet.search.presentation.SearchLauncher
import javax.inject.Inject

class SearchLauncherImpl @Inject constructor() : SearchLauncher {

    override fun route() = ROUTE

    @OptIn(ExperimentalAnimationApi::class)
    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(
            route = ROUTE,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Left,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentScope.SlideDirection.Right,
                    animationSpec = tween(ANIMATION_DURATION)
                )
            }
        ) {
            SearchScreen()
        }
    }

    companion object {
        private const val ROUTE = "search"
    }
}
