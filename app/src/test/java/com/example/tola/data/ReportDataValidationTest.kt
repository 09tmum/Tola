package com.example.tola.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ReportDataValidationTest {

    private val repository = ReportRepository(
        authProvider = { null },
        firestoreProvider = { null }
    )

    private val sampleReports = listOf(
        LostFoundReport(
            id = "1",
            itemName = "HP Pavilion Laptop",
            category = "Electronics",
            description = "Silver 15-inch laptop",
            location = "Main Library",
            type = "LOST",
            status = "OPEN"
        ),
        LostFoundReport(
            id = "2",
            itemName = "Leather Wallet",
            category = "Bags",
            description = "Brown leather wallet",
            location = "Student Centre",
            type = "FOUND",
            status = "OPEN"
        ),
        LostFoundReport(
            id = "3",
            itemName = "Campus ID Card",
            category = "IDs",
            description = "Student identity card",
            location = "Cafeteria",
            type = "LOST",
            status = "OPEN"
        )
    )

    @Test
    fun reportData_defaultStatus_isOpen() {
        val report = LostFoundReport(itemName = "Water Bottle")
        assertEquals("OPEN", report.status)
        assertEquals("LOST", report.type)
    }

    @Test
    fun updateReport_blankItemName_throwsIllegalArgumentException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            runBlocking {
                repository.updateReport(
                    reportId = "rep-101",
                    itemName = "   ",
                    description = "Updated details",
                    location = "Library",
                    category = "Other"
                )
            }
        }
        assertEquals("Item name cannot be empty.", exception.message)
    }

    @Test
    fun filter_categoryElectronics_returnsMatchingCategoryOnly() {
        val filtered = sampleReports.filter { it.category == "Electronics" }
        assertEquals(1, filtered.size)
        assertEquals("HP Pavilion Laptop", filtered.first().itemName)
    }

    @Test
    fun filter_categoryIDs_returnsMatchingCategoryOnly() {
        val filtered = sampleReports.filter { it.category == "IDs" }
        assertEquals(1, filtered.size)
        assertEquals("Campus ID Card", filtered.first().itemName)
    }

    @Test
    fun search_emptyQuery_returnsCompleteList() {
        val searchQuery = ""
        val filtered = sampleReports.filter {
            searchQuery.isBlank() || it.itemName.contains(searchQuery, ignoreCase = true)
        }
        assertEquals(3, filtered.size)
    }
}
