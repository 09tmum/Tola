package com.example.tola.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.tola.ui.screens.StudentNotificationsScreen
import com.example.tola.ui.theme.TolaTheme
import org.junit.Rule
import org.junit.Test

class NavigationUiTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun studentNotificationsScreen_emptyList_displaysEmptyState() {
        composeTestRule.setContent {
            TolaTheme {
                StudentNotificationsScreen(
                    onBack = {},
                    notifications = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText("Notifications").assertIsDisplayed()
        composeTestRule.onNodeWithText("No notifications yet").assertIsDisplayed()
        composeTestRule.onNodeWithText("You're all caught up").assertIsDisplayed()
    }
}
