package com.example.tola.data

import com.example.tola.ui.screens.LostFoundFeedItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReportSearchFilterTest {

    private val sampleItems = listOf(
        LostFoundFeedItem(
            id = "1",
            title = "Blue Hydro Flask",
            description = "Blue water bottle",
            location = "Main Library",
            timeAgo = "10 min ago",
            isFound = true,
            tagNumber = "1024"
        ),
        LostFoundFeedItem(
            id = "2",
            title = "North Face Backpack",
            description = "Black backpack with red keychain",
            location = "Computer Lab",
            timeAgo = "1 hour ago",
            isFound = false,
            tagNumber = "1023"
        ),
        LostFoundFeedItem(
            id = "3",
            title = "AirPods Pro",
            description = "White earphones in red case",
            location = "Student Centre",
            timeAgo = "2 hours ago",
            isFound = true,
            tagNumber = "1022"
        )
    )

    @Test
    fun filter_status_lostOnly_returnsOnlyLostReports() {
        val filtered = sampleItems.filter { !it.isFound }
        assertEquals(1, filtered.size)
        assertEquals("North Face Backpack", filtered.first().title)
    }

    @Test
    fun filter_status_foundOnly_returnsOnlyFoundReports() {
        val filtered = sampleItems.filter { it.isFound }
        assertEquals(2, filtered.size)
        assertTrue(filtered.all { it.isFound })
    }

    @Test
    fun search_keywordMatch_returnsMatchingItems() {
        val searchQuery = "Backpack"
        val results = sampleItems.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.location.contains(searchQuery, ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("North Face Backpack", results.first().title)
    }

    @Test
    fun search_locationMatch_returnsMatchingItems() {
        val searchQuery = "Library"
        val results = sampleItems.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.description.contains(searchQuery, ignoreCase = true) ||
            it.location.contains(searchQuery, ignoreCase = true)
        }
        assertEquals(1, results.size)
        assertEquals("Blue Hydro Flask", results.first().title)
    }
}
