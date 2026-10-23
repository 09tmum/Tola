package com.example.tola.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tola.ui.theme.*

data class FoundItemReportData(
    val itemId: String,
    val locationFound: String,
    val dateFound: String,
    val timeFound: String,
    val additionalDetails: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemReportScreen(
    itemId: String,
    onBack: () -> Unit,
    onSubmitReport: (FoundItemReportData) -> Unit,
    itemName: String? = null
) {
    var locationFound by rememberSaveable {
        mutableStateOf("")
    }
    var dateFound by rememberSaveable {
        mutableStateOf("")
    }
    var timeFound by rememberSaveable {
        mutableStateOf("")
    }
    var additionalDetails by rememberSaveable {
        mutableStateOf("")
    }
    var attemptedSubmit by rememberSaveable {
        mutableStateOf(false)
    }

    val locationError =
        attemptedSubmit && locationFound.isBlank()

    val dateError =
        attemptedSubmit && dateFound.isBlank()

    Scaffold(
        containerColor = TolaBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Report Found Item",
                        color = TolaTextPrimary
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Back", color = TolaPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = TolaBackground
                )
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Did you find this item?",
                style = MaterialTheme.typography.headlineSmall,
                color = TolaTextPrimary
            )

            Text(
                text = "Provide details about where and when " +
                        "you found the item so its owner can " +
                        "be helped with recovery.",
                style = MaterialTheme.typography.bodyMedium,
                color = TolaTextSecondary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = TolaSurface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, TolaBorder
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Original lost-item report",
                        style = MaterialTheme.typography.labelLarge,
                        color = TolaTextSecondary
                    )

                    Text(
                        text = itemName
                            ?.takeIf { it.isNotBlank() }
                            ?: "Item ID: $itemId",
                        style = MaterialTheme.typography.titleMedium,
                        color = TolaTextPrimary
                    )

                    Text(
                        text = "This report will be linked to the " +
                                "existing item.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TolaTextMuted
                    )
                }
            }

            OutlinedTextField(
                value = locationFound,
                onValueChange = { locationFound = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Where did you find it? *") },
                placeholder = {
                    Text("e.g. University library")
                },
                singleLine = true,
                isError = locationError,
                supportingText = {
                    if (locationError) {
                        Text(
                            "Please enter where you found the item.",
                            color = TolaError
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TolaPrimary,
                    unfocusedBorderColor = TolaBorderStrong,
                    focusedLabelColor = TolaPrimary,
                    cursorColor = TolaPrimary
                )
            )

            OutlinedTextField(
                value = dateFound,
                onValueChange = { dateFound = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Date found *") },
                placeholder = {
                    Text("DD/MM/YYYY")
                },
                singleLine = true,
                isError = dateError,
                supportingText = {
                    if (dateError) {
                        Text(
                            "Please enter the date the item was found.",
                            color = TolaError
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TolaPrimary,
                    unfocusedBorderColor = TolaBorderStrong,
                    focusedLabelColor = TolaPrimary,
                    cursorColor = TolaPrimary
                )
            )

            OutlinedTextField(
                value = timeFound,
                onValueChange = { timeFound = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Time found (optional)") },
                placeholder = {
                    Text("e.g. 14:30")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TolaPrimary,
                    unfocusedBorderColor = TolaBorderStrong,
                    cursorColor = TolaPrimary
                )
            )

            OutlinedTextField(
                value = additionalDetails,
                onValueChange = { additionalDetails = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Additional information") },
                placeholder = {
                    Text(
                        "Describe the item's condition " +
                                "or where it is being kept"
                    )
                },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TolaPrimary,
                    unfocusedBorderColor = TolaBorderStrong,
                    cursorColor = TolaPrimary
                )
            )

            Button(
                onClick = {
                    attemptedSubmit = true

                    if (
                        locationFound.isNotBlank() &&
                        dateFound.isNotBlank()
                    ) {
                        onSubmitReport(
                            FoundItemReportData(
                                itemId = itemId,
                                locationFound = locationFound.trim(),
                                dateFound = dateFound.trim(),
                                timeFound = timeFound.trim(),
                                additionalDetails =
                                    additionalDetails.trim()
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TolaPrimary,
                    contentColor = androidx.compose.ui.graphics.Color.White
                )
            ) {
                Text("Submit Found Report")
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, TolaBorderStrong
                )
            ) {
                Text(
                    "Cancel",
                    color = TolaPrimary
                )
            }

            Text(
                text = "* Required fields",
                style = MaterialTheme.typography.bodySmall,
                color = TolaTextMuted
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FoundItemReportScreenPreview() {
    TolaTheme {
        FoundItemReportScreen(
            itemId = "LF-1",
            onBack = {},
            onSubmitReport = {}
        )
    }
}