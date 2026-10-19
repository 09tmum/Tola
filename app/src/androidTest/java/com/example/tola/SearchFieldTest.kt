package com.example.tola

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tola.ui.screens.HomeScreen
import com.example.tola.ui.theme.TolaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchField_acceptsInput_andFiltersFeedItems() {
        composeTestRule.setContent {
            TolaTheme {
                HomeScreen()
            }
        }

        // Type "Hydro" into the search box
        composeTestRule.onNodeWithText("Search lost or found items")
            .performTextInput("Hydro")

        // Verify Hydro Flask is displayed and Backpack is filtered out
        composeTestRule.onNodeWithText("Cobalt Blue Hydro Flask").assertIsDisplayed()
        composeTestRule.onNodeWithText("North Face Recon Backpack").assertDoesNotExist()
    }

    @Test
    fun searchField_clearButton_clearsInput() {
        composeTestRule.setContent {
            TolaTheme {
                HomeScreen()
            }
        }

        // Type query
        composeTestRule.onNodeWithText("Search lost or found items")
            .performTextInput("Hydro")

        // Click clear icon
        composeTestRule.onNodeWithContentDescription("Clear search")
            .performClick()

        // Verify both items are visible again
        composeTestRule.onNodeWithText("Cobalt Blue Hydro Flask").assertIsDisplayed()
        composeTestRule.onNodeWithText("North Face Recon Backpack").assertIsDisplayed()
    }
}
