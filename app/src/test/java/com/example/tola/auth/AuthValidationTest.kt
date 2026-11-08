package com.example.tola.auth

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class AuthValidationTest {

    private val repository = FirebaseAuthRepository(
        authProvider = { null },
        firestoreProvider = { null }
    )

    @Test
    fun register_blankFullName_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.register(
                    fullName = "   ",
                    studentId = "STU-101",
                    email = "student@campus.edu",
                    phoneNumber = "0991234567",
                    password = "password123"
                )
            }
        }
        assertEquals("Enter your full name.", exception.message)
    }

    @Test
    fun register_blankStudentId_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.register(
                    fullName = "Prince Mphande",
                    studentId = "",
                    email = "student@campus.edu",
                    phoneNumber = "0991234567",
                    password = "password123"
                )
            }
        }
        assertEquals("Enter your student ID.", exception.message)
    }

    @Test
    fun register_blankEmail_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.register(
                    fullName = "Prince Mphande",
                    studentId = "STU-101",
                    email = "   ",
                    phoneNumber = "0991234567",
                    password = "password123"
                )
            }
        }
        assertEquals("Enter your email.", exception.message)
    }

    @Test
    fun register_shortPassword_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.register(
                    fullName = "Prince Mphande",
                    studentId = "STU-101",
                    email = "student@campus.edu",
                    phoneNumber = "0991234567",
                    password = "123"
                )
            }
        }
        assertEquals("Your password must contain at least 6 characters.", exception.message)
    }

    @Test
    fun login_blankEmail_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.login(email = "  ", password = "password123")
            }
        }
        assertEquals("Enter your email address.", exception.message)
    }

    @Test
    fun login_blankPassword_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.login(email = "student@campus.edu", password = "")
            }
        }
        assertEquals("Enter your password.", exception.message)
    }
}
