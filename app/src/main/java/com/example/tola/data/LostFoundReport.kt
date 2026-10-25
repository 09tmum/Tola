package com.example.tola.data

data class LostFoundReport(
    val id: String = "",
    val itemName: String = "",
    val category: String = "",
    val description: String = "",
    val location: String = "",
    val date: String = "",
    val time: String = "",
    val type: String = "LOST",
    val ownerUid: String = "",
    val status: String = "OPEN"
)
