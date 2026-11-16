package com.example.tola.data

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Date

class NotificationRepositoryTest {

    @Test
    fun formatTimeAgo_now_returnsJustNow() {
        val now = Date()
        val result = NotificationRepository.formatTimeAgo(now)
        assertEquals("Just now", result)
    }

    @Test
    fun formatTimeAgo_minutesAgo_returnsMinutesFormat() {
        val tenMinutesAgo = Date(System.currentTimeMillis() - (10 * 60 * 1000L))
        val result = NotificationRepository.formatTimeAgo(tenMinutesAgo)
        assertEquals("10 min ago", result)
    }

    @Test
    fun formatTimeAgo_hoursAgo_returnsHoursFormat() {
        val twoHoursAgo = Date(System.currentTimeMillis() - (2 * 3600 * 1000L))
        val result = NotificationRepository.formatTimeAgo(twoHoursAgo)
        assertEquals("2 hours ago", result)
    }

    @Test
    fun formatTimeAgo_daysAgo_returnsDaysFormat() {
        val threeDaysAgo = Date(System.currentTimeMillis() - (3 * 24 * 3600 * 1000L))
        val result = NotificationRepository.formatTimeAgo(threeDaysAgo)
        assertEquals("3 days ago", result)
    }
}
