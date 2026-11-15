package com.example.tola.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ClaimValidationTest {

    private val repository = ClaimRepository(
        authProvider = { null },
        firestoreProvider = { null }
    )

    @Test
    fun submitClaim_blankProofDescription_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.submitClaim(itemId = "item-101", proofDescription = "")
            }
        }
        assertEquals("Please provide ownership evidence between 1 and 400 characters.", exception.message)
    }

    @Test
    fun submitClaim_blankItemId_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.submitClaim(itemId = "", proofDescription = "Unique sticker on top")
            }
        }
        assertEquals("The item ID is missing.", exception.message)
    }

    @Test
    fun decideClaim_blankClaimId_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.decideClaim(claimId = "", approve = true)
            }
        }
        assertEquals("The claim ID is missing.", exception.message)
    }
}
