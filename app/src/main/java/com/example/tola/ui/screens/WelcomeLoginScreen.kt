package com.example.tola.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tola.auth.FirebaseAuthRepository
import com.example.tola.ui.TolaUserRole
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaTheme
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.launch

private fun isValidEmail(email: String): Boolean {
    return email.isNotBlank() &&
            Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                .matches(email.trim())
}

private fun authErrorMessage(error: Exception): String {
    return when (error) {
        is FirebaseAuthUserCollisionException ->
            "An account with this email already exists. Please log in."

        is FirebaseAuthWeakPasswordException ->
            "Your password is too weak. Use at least 6 characters."

        is FirebaseAuthInvalidCredentialsException ->
            "The email address or credentials are invalid."

        is FirebaseAuthInvalidUserException ->
            "No account was found for these credentials. Please check your details."

        is FirebaseNetworkException ->
            "Network error. Check your internet connection and try again."

        is FirebaseFirestoreException -> {
            when (error.code) {
                FirebaseFirestoreException.Code.PERMISSION_DENIED ->
                    "Firestore access was denied. Check your security rules."

                FirebaseFirestoreException.Code.UNAVAILABLE ->
                    "The service is temporarily unavailable. Try again."

                else ->
                    "We couldn't save or retrieve your profile. Please try again."
            }
        }

        is IllegalArgumentException ->
            error.message ?: "Please check the information you entered."

        else ->
            "Something went wrong. Please try again."
    }
}

@Composable
fun WelcomeLoginScreen(
    onLoginSuccess: (TolaUserRole) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Login fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Registration fields
    var regFullName by remember { mutableStateOf("") }
    var regStudentId by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhoneNumber by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regConfirmPasswordVisible by remember { mutableStateOf(false) }

    // Firebase operation state
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var messageIsError by remember { mutableStateOf(true) }

    val repository = remember { FirebaseAuthRepository() }
    val scope = rememberCoroutineScope()

    val isLoginEmailValid = isValidEmail(email)
    val isRegEmailValid = isValidEmail(regEmail)
    val passwordsMatch = regPassword == regConfirmPassword

    val isRegisterValid =
        regFullName.trim().isNotBlank() &&
                regStudentId.trim().isNotBlank() &&
                isRegEmailValid &&
                regPassword.length >= 6 &&
                regConfirmPassword.isNotBlank() &&
                passwordsMatch

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Tola logo
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(TolaPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tola Logo",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Welcome to Tola",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Campus Lost & Found made effortless.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = {
                        selectedTab = 0
                        message = null
                    },
                    text = { Text("Login") }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = {
                        selectedTab = 1
                        message = null
                    },
                    text = { Text("Register") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == 0) {
                // Login email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it.filter { char -> !char.isWhitespace() }
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = email.isNotEmpty() && !isLoginEmailValid,
                    supportingText = {
                        if (email.isNotEmpty() && !isLoginEmailValid) {
                            Text("Enter a valid email address.")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Login password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    singleLine = true,
                    enabled = !isLoading,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                passwordVisible = !passwordVisible
                            },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (passwordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            enabled = !isLoading
                        )

                        Text(
                            text = "Remember me",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Text(
                        text = "Forgot Password?",
                        color = TolaPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        message = null

                        scope.launch {
                            isLoading = true

                            try {
                                val role = repository.login(
                                    email = email.trim(),
                                    password = password
                                )

                                messageIsError = false
                                message = "Login successful."
                                onLoginSuccess(role)
                            } catch (error: Exception) {
                                messageIsError = true
                                message = authErrorMessage(error)
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = isLoginEmailValid &&
                            password.isNotBlank() &&
                            !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TolaPrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            } else {
                // Registration form
                Text(
                    text = "Create your Tola account",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Register to report and find lost items on campus.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = regFullName,
                    onValueChange = {
                        regFullName = it
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full Name *") },
                    placeholder = { Text("Enter your full name") },
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regStudentId,
                    onValueChange = {
                        regStudentId = it
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Student ID *") },
                    placeholder = { Text("e.g. STU-10293") },
                    singleLine = true,
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regEmail,
                    onValueChange = {
                        regEmail = it.filter { char -> !char.isWhitespace() }
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email *") },
                    placeholder = { Text("Enter your email") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = regEmail.isNotEmpty() && !isRegEmailValid,
                    supportingText = {
                        if (regEmail.isNotEmpty() && !isRegEmailValid) {
                            Text("Enter a valid email address.")
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regPhoneNumber,
                    onValueChange = { regPhoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Phone Number (optional)") },
                    placeholder = { Text("Enter your phone number") },
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regPassword,
                    onValueChange = {
                        regPassword = it
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password *") },
                    placeholder = { Text("Create a password") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = regPassword.isNotEmpty() &&
                            regPassword.length < 6,
                    supportingText = {
                        if (regPassword.isNotEmpty() &&
                            regPassword.length < 6
                        ) {
                            Text("Use at least 6 characters.")
                        }
                    },
                    visualTransformation = if (regPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                regPasswordVisible = !regPasswordVisible
                            },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = if (regPasswordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription = if (regPasswordVisible) {
                                    "Hide password"
                                } else {
                                    "Show password"
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = regConfirmPassword,
                    onValueChange = {
                        regConfirmPassword = it
                        message = null
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password *") },
                    placeholder = { Text("Re-enter your password") },
                    singleLine = true,
                    enabled = !isLoading,
                    isError = regConfirmPassword.isNotEmpty() &&
                            !passwordsMatch,
                    supportingText = {
                        if (regConfirmPassword.isNotEmpty() &&
                            !passwordsMatch
                        ) {
                            Text(
                                "Passwords do not match.",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    },
                    visualTransformation = if (regConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                regConfirmPasswordVisible =
                                    !regConfirmPasswordVisible
                            },
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = if (regConfirmPasswordVisible) {
                                    Icons.Default.VisibilityOff
                                } else {
                                    Icons.Default.Visibility
                                },
                                contentDescription =
                                    if (regConfirmPasswordVisible) {
                                        "Hide password"
                                    } else {
                                        "Show password"
                                    }
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        message = null

                        scope.launch {
                            isLoading = true

                            try {
                                repository.register(
                                    fullName = regFullName.trim(),
                                    studentId = regStudentId.trim(),
                                    email = regEmail.trim(),
                                    phoneNumber = regPhoneNumber.trim(),
                                    password = regPassword
                                )

                                repository.logout()

                                email = regEmail.trim()
                                password = ""
                                selectedTab = 0

                                messageIsError = false
                                message = "Account created successfully! Please log in with your credentials."
                            } catch (error: Exception) {
                                messageIsError = true
                                message = authErrorMessage(error)
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    enabled = isRegisterValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TolaPrimary,
                        contentColor = Color.White
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Create Account",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Firebase status and error messages
            if (message != null) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = message.orEmpty(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    color = if (messageIsError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.primary
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tola • Campus Lost & Found",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeLoginScreenPreview() {
    TolaTheme {
        WelcomeLoginScreen(onLoginSuccess = {})
    }
}