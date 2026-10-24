package com.example.tola.ui

enum class TolaUserRole {
    STUDENT,
    ADMIN
}

fun parseTolaUserRole(value: String?): TolaUserRole? {
    return when (value?.trim()?.uppercase()) {
        "STUDENT" -> TolaUserRole.STUDENT
        "ADMIN" -> TolaUserRole.ADMIN
        else -> null
    }
}