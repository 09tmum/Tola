package com.example.tola.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.tola.ui.screens.ReportItemScreen
import com.example.tola.ui.theme.TolaTheme
import org.junit.Rule
import org.junit.Test

class ReportFormValidationUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun reportItemScreen_initialState_displaysHeaderAndStatusSelection() {
        composeTestRule.setContent {
            TolaTheme {
                ReportItemScreen(
                    initialIsLost = true,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Report Item").assertIsDisplayed()
        composeTestRule.onNodeWithText("I Lost It").assertIsDisplayed()
        composeTestRule.onNodeWithText("I Found It").assertIsDisplayed()
        composeTestRule.onNodeWithText("Report Lost Item").assertIsDisplayed()
    }
}
