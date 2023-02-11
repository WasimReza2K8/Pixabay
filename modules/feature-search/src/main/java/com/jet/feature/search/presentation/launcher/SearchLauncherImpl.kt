package com.jet.feature.search.presentation.launcher

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.jet.feature.search.presentation.view.SearchScreen
import com.jet.search.presentation.SearchLauncher
import javax.inject.Inject

class SearchLauncherImpl @Inject constructor() : SearchLauncher {

    override fun route() = ROUTE

    override fun registerGraph(navGraphBuilder: NavGraphBuilder) {
        navGraphBuilder.composable(ROUTE) {
            SearchScreen()
        }
    }

    companion object {
        private const val ROUTE = "search"
    }
}
