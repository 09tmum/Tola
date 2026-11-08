package com.example.tola.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RoleGateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun roleGate_studentAccessingAdminContent_triggersAccessDenied() {
        var accessDeniedCalled = false

        composeTestRule.setContent {
            RoleGate(
                currentRole = TolaUserRole.STUDENT,
                requiredRole = TolaUserRole.ADMIN,
                onAccessDenied = {
                    accessDeniedCalled = true
                }
            ) {
                Text("Secret Admin Dashboard")
            }
        }

        composeTestRule.onNodeWithText("Secret Admin Dashboard").assertDoesNotExist()
        assertTrue(accessDeniedCalled)
    }

    @Test
    fun roleGate_adminAccessingAdminContent_rendersContent() {
        composeTestRule.setContent {
            RoleGate(
                currentRole = TolaUserRole.ADMIN,
                requiredRole = TolaUserRole.ADMIN,
                onAccessDenied = {}
            ) {
                Text("Secret Admin Dashboard")
            }
        }

        composeTestRule.onNodeWithText("Secret Admin Dashboard").assertIsDisplayed()
    }
}
