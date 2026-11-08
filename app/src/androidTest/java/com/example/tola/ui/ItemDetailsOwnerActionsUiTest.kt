package com.example.tola.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.tola.ui.screens.ItemDetailsScreen
import com.example.tola.ui.theme.TolaTheme
import org.junit.Rule
import org.junit.Test

class ItemDetailsOwnerActionsUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun itemDetailsScreen_initialState_displaysItemHeader() {
        composeTestRule.setContent {
            TolaTheme {
                ItemDetailsScreen(
                    itemId = "non-existent-sample-id",
                    onBack = {},
                    onClaimClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Item Details").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your Report Actions").assertDoesNotExist()
    }
}
