package com.example.wasim.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.core.R.string.unknown_error_detail
import com.example.core.ui.R
import com.example.wasim.MainActivity
import com.example.wasim.search.FakeSearchRepository
import com.example.wasim.search.FakeSearchRepository.ReturnType.UnknownException
import com.example.wasim.search.FakeSearchRepository.ReturnType.Valid
import com.jet.feature.search.R.string
import com.jet.feature.search.di.SearchDomainModule
import com.jet.search.domain.repository.SearchRepository
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@MediumTest
@UninstallModules(SearchDomainModule::class)
@RunWith(AndroidJUnit4::class)
class DetailScreenTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val androidComposeTestRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var fakeRepository: SearchRepository

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun test_detail_screen_containing_photo() {
        (fakeRepository as? FakeSearchRepository)?.setReturnType(Valid)

        androidComposeTestRule.apply {
            onNodeWithTag(activity.getString(string.search_item)).performClick()
            onNodeWithText(activity.getString(string.search_confirmation_yes)).performClick()
            onNodeWithTag(activity.getString(R.string.photo_text_view)).assertIsDisplayed()
        }
    }

    @Test
    fun test_detail_screen_having_unknown_error() {
        (fakeRepository as? FakeSearchRepository)?.setReturnType(Valid)

        androidComposeTestRule.apply {
            onNodeWithTag(activity.getString(string.search_item)).performClick()
            (fakeRepository as? FakeSearchRepository)?.setReturnType(UnknownException)
            onNodeWithText(activity.getString(string.search_confirmation_yes)).performClick()
            onNodeWithText(activity.getString(unknown_error_detail)).assertIsDisplayed()
        }
    }
}