package com.example.tola

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tola.ui.screens.HomeScreen
import com.example.tola.ui.screens.ReportItemScreen
import com.example.tola.ui.theme.TolaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportItemButtonTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun reportLostButton_triggersCallback() {
        var reportLostClicked = false

        composeTestRule.setContent {
            TolaTheme {
                HomeScreen(
                    onReportLost = { reportLostClicked = true },
                    onReportFound = {},
                    onItemClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Report Lost").performClick()
        assertTrue("Report Lost button should fire onReportLost callback", reportLostClicked)
    }

    @Test
    fun reportFoundButton_triggersCallback() {
        var reportFoundClicked = false

        composeTestRule.setContent {
            TolaTheme {
                HomeScreen(
                    onReportLost = {},
                    onReportFound = { reportFoundClicked = true },
                    onItemClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Report Found").performClick()
        assertTrue("Report Found button should fire onReportFound callback", reportFoundClicked)
    }

    @Test
    fun reportItemScreen_initialIsLostFalse_displaysFoundSection() {
        composeTestRule.setContent {
            TolaTheme {
                ReportItemScreen(
                    initialIsLost = false,
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("I Found It").assertExists()
        composeTestRule.onNodeWithText("Report Found Item").assertExists()
    }
}
