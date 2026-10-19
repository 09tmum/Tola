package com.example.tola

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tola.ui.screens.WelcomeLoginScreen
import com.example.tola.ui.theme.TolaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WelcomeLoginValidationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun invalidEmail_showsError_andDisablesLoginButton() {
        composeTestRule.setContent {
            TolaTheme {
                WelcomeLoginScreen(onLoginSuccess = {})
            }
        }

        // Clear default email and enter invalid email
        composeTestRule.onNodeWithText("flashback.prince@campus.edu").performTextClearance()
        composeTestRule.onNodeWithText("Enter your email").performTextInput("invalidemail")

        // Verify error text is displayed and Login button is disabled
        composeTestRule.onNodeWithText("Please enter a valid email address (e.g. user@campus.edu)")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Login").assertIsNotEnabled()
    }

    @Test
    fun validEmail_enablesLoginButton() {
        composeTestRule.setContent {
            TolaTheme {
                WelcomeLoginScreen(onLoginSuccess = {})
            }
        }

        // Clear default email and enter valid email
        composeTestRule.onNodeWithText("flashback.prince@campus.edu").performTextClearance()
        composeTestRule.onNodeWithText("Enter your email").performTextInput("alex.chen@campus.edu")

        // Verify Login button is enabled
        composeTestRule.onNodeWithText("Login").assertIsEnabled()
    }
}
