package com.jet.feature.detail.presentation.launcher

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType

import androidx.navigation.navArgument
import com.example.core.ui.utils.ANIMATION_DURATION
import com.google.accompanist.navigation.animation.composable
import com.jet.detail.presentation.DetailLauncher
import com.jet.feature.detail.presentation.view.DetailScreen
import javax.inject.Inject

class DetailLauncherImpl @Inject constructor() : DetailLauncher {

    override fun route(localId: String) = "$ROUTE/$localId"

    @OptIn(ExperimentalAnimationApi::class)
    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(
            route = "$ROUTE/{$LOCAL_ID}",
            arguments = listOf(navArgument(LOCAL_ID) { type = NavType.StringType }),
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
            DetailScreen()
        }
    }

    companion object {
        private const val ROUTE = "detail"
        const val LOCAL_ID = "local_id"
    }
}
