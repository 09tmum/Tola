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
import com.example.tola.ui.TolaUserRole
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaTheme

private fun isValidEmail(email: String): Boolean {
    return email.isNotBlank() && Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
}

@Composable
fun WelcomeLoginScreen(onLoginSuccess: (TolaUserRole) -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }

    // Login state
    var email by remember { mutableStateOf("flashback.prince@campus.edu") }
    var password by remember { mutableStateOf("password123") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }

    // Register state
    var regFullName by remember { mutableStateOf("") }
    var regStudentId by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPhoneNumber by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var regPasswordVisible by remember { mutableStateOf(false) }
    var regConfirmPasswordVisible by remember { mutableStateOf(false) }

    val isLoginEmailValid = isValidEmail(email)
    val isRegEmailValid = isValidEmail(regEmail)
    val passwordsMatch = regPassword == regConfirmPassword

    val isRegisterValid = regFullName.trim().isNotBlank() &&
            regStudentId.trim().isNotBlank() &&
            isRegEmailValid &&
            regPassword.isNotBlank() &&
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

            // Tola Logo
            Box(
                modifier = Modifier.size(64.dp).clip(CircleShape).background(TolaPrimary),
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
            Text("Welcome to Tola", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Campus Lost & Found made effortless.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Login / Register Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Login") }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Register") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (selectedTab == 0) {
                // Email input
                OutlinedTextField(
                    value = email,
                    onValueChange = { input -> email = input.filter { !it.isWhitespace() } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email") },
                    placeholder = { Text("Enter your email") },
                    singleLine = true,
                    isError = email.isNotEmpty() && !isLoginEmailValid,
                    supportingText = {
                        if (email.isNotEmpty() && !isLoginEmailValid) {
                            Text("Please enter a valid email address (e.g. user@campus.edu)")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    placeholder = { Text("Enter your password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Remember me + Forgot password
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = rememberMe, onCheckedChange = { rememberMe = it })
                        Text("Remember me", style = MaterialTheme.typography.bodyMedium)
                    }

                    Text(
                        text = "Forgot Password?",
                        color = TolaPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Login Button
                Button(
                    onClick = {
                        if (isLoginEmailValid) {
                            val role = if (email.contains("admin", ignoreCase = true)) TolaUserRole.ADMIN else TolaUserRole.STUDENT
                            onLoginSuccess(role)
                        }
                    },
                    enabled = isLoginEmailValid,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TolaPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Login", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))

            } else {
                // Register UI
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

                // Full Name input (Required)
                OutlinedTextField(
                    value = regFullName,
                    onValueChange = { regFullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Full Name *") },
                    placeholder = { Text("Enter your full name") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Student ID input (Required)
                OutlinedTextField(
                    value = regStudentId,
                    onValueChange = { regStudentId = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Student ID *") },
                    placeholder = { Text("e.g. STU-10293") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Register Email input (Required)
                OutlinedTextField(
                    value = regEmail,
                    onValueChange = { input -> regEmail = input.filter { !it.isWhitespace() } },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email *") },
                    placeholder = { Text("Enter your email") },
                    singleLine = true,
                    isError = regEmail.isNotEmpty() && !isRegEmailValid,
                    supportingText = {
                        if (regEmail.isNotEmpty() && !isRegEmailValid) {
                            Text("Please enter a valid email address (e.g. user@campus.edu)")
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number input (Optional)
                OutlinedTextField(
                    value = regPhoneNumber,
                    onValueChange = { regPhoneNumber = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Phone Number (optional)") },
                    placeholder = { Text("e.g. +1 555-0199") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Register Password input (Required)
                OutlinedTextField(
                    value = regPassword,
                    onValueChange = { regPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password *") },
                    placeholder = { Text("Create a password") },
                    singleLine = true,
                    visualTransformation = if (regPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { regPasswordVisible = !regPasswordVisible }) {
                            Icon(
                                imageVector = if (regPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (regPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password input (Required)
                OutlinedTextField(
                    value = regConfirmPassword,
                    onValueChange = { regConfirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password *") },
                    placeholder = { Text("Re-enter your password") },
                    singleLine = true,
                    isError = regConfirmPassword.isNotEmpty() && !passwordsMatch,
                    supportingText = {
                        if (regConfirmPassword.isNotEmpty() && !passwordsMatch) {
                            Text("Passwords do not match", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    visualTransformation = if (regConfirmPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    trailingIcon = {
                        IconButton(onClick = { regConfirmPasswordVisible = !regConfirmPasswordVisible }) {
                            Icon(
                                imageVector = if (regConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (regConfirmPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (isRegisterValid) {
                            val role = if (regEmail.contains("admin", ignoreCase = true)) TolaUserRole.ADMIN else TolaUserRole.STUDENT
                            onLoginSuccess(role)
                        }
                    },
                    enabled = isRegisterValid,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TolaPrimary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Create Account", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

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
