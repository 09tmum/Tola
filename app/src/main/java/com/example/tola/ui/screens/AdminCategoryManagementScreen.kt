package com.example.tola.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tola.ui.theme.TolaBorderStrong
import com.example.tola.ui.theme.TolaError
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaTextPrimary
import com.example.tola.ui.theme.TolaTextSecondary
import com.example.tola.ui.theme.TolaTheme

@Composable
fun AdminCategoryManagementScreen(
    categories: List<AdminCategory> = emptyList(),
    onBack: () -> Unit = {},
    onAddCategory: (String) -> Unit = {},
    onEditCategory: (AdminCategory) -> Unit = {},
    onDeleteCategory: (AdminCategory) -> Unit = {}
) {
    var newCategoryName by rememberSaveable { mutableStateOf("") }
    var attemptedAdd by rememberSaveable { mutableStateOf(false) }
    val nameError = attemptedAdd && newCategoryName.isBlank()

    AdminScreenLayout("Category Management", "Maintain categories used in lost and found reports.", onBack) {
        AdminPanelCard {
            Text("Add a category", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
            OutlinedTextField(
                value = newCategoryName,
                onValueChange = { newCategoryName = it; attemptedAdd = false },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Category name") },
                placeholder = { Text("e.g. Identification Cards") },
                singleLine = true,
                isError = nameError,
                supportingText = { if (nameError) Text("Enter a category name.", color = TolaError) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TolaPrimary,
                    unfocusedBorderColor = TolaBorderStrong,
                    focusedLabelColor = TolaPrimary,
                    cursorColor = TolaPrimary
                )
            )
            Button(
                onClick = {
                    attemptedAdd = true
                    if (newCategoryName.isNotBlank()) {
                        onAddCategory(newCategoryName.trim())
                        newCategoryName = ""
                        attemptedAdd = false
                    }
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TolaPrimary,
                    contentColor = Color.White
                )
            ) { Text("Add category", fontWeight = FontWeight.Bold, color = Color.White) }
        }

        Text("Existing categories (${categories.size})", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
        if (categories.isEmpty()) {
            AdminEmptyState("No categories are connected yet.")
        } else {
            categories.forEach { category ->
                AdminPanelCard {
                    Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = TolaTextPrimary)
                    Text("${category.itemCount} associated item(s)", style = MaterialTheme.typography.bodyMedium, color = TolaTextSecondary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onEditCategory(category) }, shape = RoundedCornerShape(10.dp)) {
                            Text("Edit", color = TolaPrimary, fontWeight = FontWeight.SemiBold)
                        }
                        OutlinedButton(onClick = { onDeleteCategory(category) }, shape = RoundedCornerShape(10.dp)) {
                            Text("Delete", color = TolaError, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}

@Preview(name = "Admin Category Management", showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun AdminCategoryManagementPreview() {
    TolaTheme { AdminCategoryManagementScreen(categories = previewAdminCategories) }
}
