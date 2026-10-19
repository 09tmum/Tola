package com.example.tola

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class FormValidationTest {

    private fun isValidEmail(email: String): Boolean {
        return email.isNotBlank() && Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
    }

    private fun filterEmailInput(input: String): String {
        return input.filter { !it.isWhitespace() }
    }

    @Test
    fun validEmail_returnsTrue() {
        assertTrue(isValidEmail("flashback.prince@campus.edu"))
        assertTrue(isValidEmail("alex.chen@campus.edu"))
        assertTrue(isValidEmail("student123@university.ac.uk"))
        assertTrue(isValidEmail("user.name+tag@domain.org"))
    }

    @Test
    fun invalidEmail_returnsFalse() {
        assertFalse(isValidEmail(""))
        assertFalse(isValidEmail("invalidemail"))
        assertFalse(isValidEmail("user@"))
        assertFalse(isValidEmail("@domain.com"))
        assertFalse(isValidEmail("user@domain"))
        assertFalse(isValidEmail("user@.com"))
    }

    @Test
    fun filterEmailInput_removesWhitespace() {
        assertEquals("alex.chen@campus.edu", filterEmailInput(" alex.chen @ campus.edu "))
        assertEquals("user@domain.com", filterEmailInput("user @ domain .com"))
    }

    @Test
    fun reportItemFormValidation_requiresItemNameAndLocation() {
        fun isReportFormValid(itemName: String, location: String): Boolean {
            return itemName.isNotBlank() && location.isNotBlank()
        }

        assertTrue(isReportFormValid("Blue Bottle", "Main Library"))
        assertFalse(isReportFormValid("", "Main Library"))
        assertFalse(isReportFormValid("Blue Bottle", ""))
        assertFalse(isReportFormValid("  ", "  "))
    }
}
