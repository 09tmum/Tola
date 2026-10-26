package com.example.tola.auth

import com.example.tola.ui.TolaUserRole
import com.example.tola.ui.parseTolaUserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val authProvider: () -> FirebaseAuth? = {
        try { FirebaseAuth.getInstance() } catch (_: Exception) { null }
    },
    private val firestoreProvider: () -> FirebaseFirestore? = {
        try { FirebaseFirestore.getInstance() } catch (_: Exception) { null }
    }
) {
    private val auth: FirebaseAuth?
        get() = authProvider()

    private val firestore: FirebaseFirestore?
        get() = firestoreProvider()

    suspend fun register(
        fullName: String,
        studentId: String,
        email: String,
        phoneNumber: String,
        password: String
    ): TolaUserRole {
        val currentAuth = auth
            ?: throw IllegalStateException("Firebase Authentication is not initialized.")
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        val cleanName = fullName.trim()
        val cleanStudentId = studentId.trim()
        val cleanEmail = email.trim()

        require(cleanName.isNotBlank()) { "Enter your full name." }
        require(cleanStudentId.isNotBlank()) { "Enter your student ID." }
        require(cleanEmail.isNotBlank()) { "Enter your email." }
        require(password.length >= 6) {
            "Your password must contain at least 6 characters."
        }

        val authResult = currentAuth.createUserWithEmailAndPassword(
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
            currentFirestore.collection("users")
                .document(user.uid)
                .set(profile)
                .await()
        } catch (error: Exception) {
            currentAuth.signOut()
            try {
                user.delete().await()
            } catch (_: Exception) {
            }
            throw error
        }

        return TolaUserRole.STUDENT
    }

    suspend fun login(
        email: String,
        password: String
    ): TolaUserRole {
        val currentAuth = auth
            ?: throw IllegalStateException("Firebase Authentication is not initialized.")
        val currentFirestore = firestore
            ?: throw IllegalStateException("Firebase Firestore is not initialized.")

        require(email.isNotBlank()) { "Enter your email address." }
        require(password.isNotBlank()) { "Enter your password." }

        currentAuth.signInWithEmailAndPassword(email.trim(), password).await()

        val user = currentAuth.currentUser
            ?: throw IllegalStateException("Login failed. Please try again.")

        try {
            val snapshot = currentFirestore.collection("users")
                .document(user.uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                currentAuth.signOut()
                throw IllegalStateException(
                    "Your user profile was not found. Please contact support."
                )
            }

            val status = snapshot.getString("accountStatus") ?: "ACTIVE"
            if (status != "ACTIVE") {
                currentAuth.signOut()
                throw IllegalStateException(
                    "This account is not active. Please contact support."
                )
            }

            val role = parseTolaUserRole(snapshot.getString("role"))
                ?: run {
                    currentAuth.signOut()
                    throw IllegalStateException(
                        "Your account role is invalid. Please contact support."
                    )
                }

            return role
        } catch (error: Exception) {
            if (currentAuth.currentUser != null) {
                currentAuth.signOut()
            }
            throw error
        }
    }

    fun logout() {
        auth?.signOut()
    }

    suspend fun getCurrentUserProfile(): UserProfile? {
        val currentAuth = auth ?: return null
        val currentFirestore = firestore ?: return null
        val user = currentAuth.currentUser ?: return null
        return try {
            val snapshot = currentFirestore.collection("users")
                .document(user.uid)
                .get()
                .await()

            if (!snapshot.exists()) {
                UserProfile(
                    uid = user.uid,
                    fullName = user.displayName.orEmpty().ifBlank { "Campus Student" },
                    email = user.email.orEmpty()
                )
            } else {
                UserProfile(
                    uid = user.uid,
                    fullName = snapshot.getString("fullName").orEmpty().ifBlank { "Campus Student" },
                    studentId = snapshot.getString("studentId").orEmpty(),
                    email = snapshot.getString("email") ?: user.email.orEmpty(),
                    phoneNumber = snapshot.getString("phoneNumber").orEmpty(),
                    role = snapshot.getString("role") ?: "STUDENT"
                )
            }
        } catch (_: Exception) {
            UserProfile(
                uid = user.uid,
                fullName = user.displayName.orEmpty().ifBlank { "Campus Student" },
                email = user.email.orEmpty()
            )
        }
    }
}
