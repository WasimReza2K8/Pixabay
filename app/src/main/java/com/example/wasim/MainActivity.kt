package com.example.wasim

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.core.navigation.Navigator
import com.example.core.navigation.NavigatorEvent
import com.example.core.ui.theme.WasimTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var navigator: Navigator

    @Inject
    lateinit var featureProvider: FeatureProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WasimTheme {
                AppContent(
                    rememberNavController(),
                    navigator,
                    featureProvider,
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppContent(
    navHostController: NavHostController,
    navigator: Navigator,
    featureProvider: FeatureProvider,
) {
    WasimTheme {
        val keyboardController = LocalSoftwareKeyboardController.current
        LaunchedEffect(navHostController) {
            navigator.destinations.onEach { event ->
                Timber.d(
                    "backQueue = ${navHostController.backQueue.map { "route = ${it.destination.route}" }}"
                )
                keyboardController?.hide()
                event?.let { navigationEvent ->
                    when (navigationEvent) {
                        is NavigatorEvent.Directions -> navHostController.navigate(
                            navigationEvent.destination,
                            navigationEvent.builder,
                        )
                        is NavigatorEvent.NavigateUp -> navHostController.navigateUp()
                    }
                }
            }.launchIn(this)
        }
        AppNavGraph(
            navController = navHostController,
            featureProvider = featureProvider,
        )
    }
}
