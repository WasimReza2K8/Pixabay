package com.example.wasim

import com.jet.detail.presentation.DetailLauncher
import com.jet.search.presentation.SearchLauncher
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FeatureProvider @Inject constructor(
    detailLauncher: DetailLauncher,
    val searchLauncher: SearchLauncher,
) {
    val launchers = listOf(searchLauncher, detailLauncher)
}
