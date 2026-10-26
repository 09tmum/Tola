package com.example.tola.auth

data class UserProfile(
    val uid: String = "",
    val fullName: String = "",
    val studentId: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val role: String = "STUDENT"
) {
    val initials: String
        get() {
            if (fullName.isBlank()) return "U"
            val parts = fullName.trim().split("\\s+".toRegex())
            return if (parts.size >= 2 && parts[0].isNotEmpty() && parts[1].isNotEmpty()) {
                "${parts[0].first().uppercaseChar()}${parts[1].first().uppercaseChar()}"
            } else if (parts.isNotEmpty() && parts[0].isNotEmpty()) {
                parts[0].take(2).uppercase()
            } else {
                "U"
            }
        }

    val firstName: String
        get() {
            if (fullName.isBlank()) return "Student"
            return fullName.trim().split("\\s+".toRegex()).firstOrNull() ?: "Student"
        }
}
