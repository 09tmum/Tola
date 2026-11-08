package com.example.tola.auth

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class UserProfileTest {

    private val repository = FirebaseAuthRepository(
        authProvider = { null },
        firestoreProvider = { null }
    )

    @Test
    fun profile_initials_twoNames_returnsFirstLetters() {
        val profile = UserProfile(fullName = "Prince Mphande")
        assertEquals("PM", profile.initials)
    }

    @Test
    fun profile_initials_singleName_returnsFirstTwoLetters() {
        val profile = UserProfile(fullName = "Prince")
        assertEquals("PR", profile.initials)
    }

    @Test
    fun profile_initials_emptyName_returnsDefault() {
        val profile = UserProfile(fullName = "")
        assertEquals("U", profile.initials)
    }

    @Test
    fun profile_firstName_returnsFirstWord() {
        val profile = UserProfile(fullName = "Prince Mphande")
        assertEquals("Prince", profile.firstName)
    }

    @Test
    fun profile_update_blankFullName_throwsException() {
        val exception = assertThrows(IllegalStateException::class.java) {
            runBlocking {
                repository.updateUserProfile(fullName = "   ", phoneNumber = "0991234567")
            }
        }
        assertEquals("Firebase Authentication is not initialized.", exception.message)
    }
}
