package com.example.tola.auth

import com.example.tola.ui.TolaUserRole
import com.example.tola.ui.parseTolaUserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    suspend fun register(
        fullName: String,
        studentId: String,
        email: String,
        phoneNumber: String,
        password: String
    ): TolaUserRole {
        val cleanName = fullName.trim()
        val cleanStudentId = studentId.trim()
        val cleanEmail = email.trim()

        require(cleanName.isNotBlank()) { "Enter your full name." }
        require(cleanStudentId.isNotBlank()) { "Enter your student ID." }
        require(cleanEmail.isNotBlank()) { "Enter your email." }
        require(password.length >= 6) {
            "Your password must contain at least 6 characters."
        }

        val authResult = auth.createUserWithEmailAndPassword(
            cleanEmail,
            password
        ).await()

        val user = authResult.user
            ?: throw IllegalStateException("Account creation failed. Please try again.")

        val profile = hashMapOf(
            "fullName" to cleanName,
            "studentId" to cleanStudentId,
            "email" to cleanEmail,
            "phoneNumber" to phoneNumber.trim(),
            "role" to "STUDENT",
            "accountStatus" to "ACTIVE",
            "createdAt" to FieldValue.serverTimestamp()
        )

        try {
            firestore.collection("users")
                .document(user.uid)
                .set(profile)
                .await()
        } catch (error: Exception) {
            // Avoid leaving an authenticated account without its profile.
            auth.signOut()
            try {
                user.delete().await()
            } catch (_: Exception) {
                // If cleanup fails, report the original Firestore error.
            }
            throw error
        }

        return TolaUserRole.STUDENT
    }

    suspend fun login(
        email: String,
        password: String
    ): TolaUserRole {
        require(email.isNotBlank()) { "Enter your email address." }
        require(password.isNotBlank()) { "Enter your password." }

        auth.signInWithEmailAndPassword(email.trim(), password).await()

        val user = auth.currentUser
            ?: throw IllegalStateException("Login failed. Please try again.")

        try {
            val snapshot = firestore.collection("users")
                .document(user.uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                auth.signOut()
                throw IllegalStateException(
                    "Your user profile was not found. Please contact support."
                )
            }

            val status = snapshot.getString("accountStatus") ?: "ACTIVE"
            if (status != "ACTIVE") {
                auth.signOut()
                throw IllegalStateException(
                    "This account is not active. Please contact support."
                )
            }

            val role = parseTolaUserRole(snapshot.getString("role"))
                ?: run {
                    auth.signOut()
                    throw IllegalStateException(
                        "Your account role is invalid. Please contact support."
                    )
                }

            return role
        } catch (error: Exception) {
            if (auth.currentUser != null) {
                auth.signOut()
            }
            throw error
        }
    }

    fun logout() {
        auth.signOut()
    }
}
