package com.example.tola.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tola.ui.theme.TolaBackground
import com.example.tola.ui.theme.TolaBorder
import com.example.tola.ui.theme.TolaBorderStrong
import com.example.tola.ui.theme.TolaError
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaSurface
import com.example.tola.ui.theme.TolaTextMuted
import com.example.tola.ui.theme.TolaTextPrimary
import com.example.tola.ui.theme.TolaTextSecondary
import com.example.tola.ui.theme.TolaTheme

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.platform.LocalContext

data class FoundItemReportData(
    val itemId: String,
    val locationFound: String,
    val dateFound: String,
    val timeFound: String,
    val additionalDetails: String,
    val imageUrl: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoundItemReportScreen(
    itemId: String,
    onBack: () -> Unit,
    onSubmitReport: (FoundItemReportData) -> Unit,
    itemName: String? = null
) {
    var locationFound by rememberSaveable { mutableStateOf("") }
    var dateFound by rememberSaveable { mutableStateOf("") }
    var timeFound by rememberSaveable { mutableStateOf("") }
    var additionalDetails by rememberSaveable { mutableStateOf("") }
    var attemptedSubmit by rememberSaveable { mutableStateOf(false) }
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType == null || mimeType.startsWith("image/")) {
                selectedImageUri = uri
                Toast.makeText(context, "Photo attached successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Invalid file type. Please select an image file (.jpg, .png).", Toast.LENGTH_LONG).show()
            }
        }
    }

    val locationError = attemptedSubmit && locationFound.isBlank()
    val dateError = attemptedSubmit && dateFound.isBlank()

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            formatter.timeZone = TimeZone.getTimeZone("UTC")
                            dateFound = formatter.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = 12,
            initialMinute = 0,
            is24Hour = false
        )
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        val timeFormatter = SimpleDateFormat("h:mm a", Locale.getDefault())
                        timeFound = timeFormatter.format(cal.time)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Select time") }
        ) {
            TimePicker(state = timePickerState)
        }
    }

    Scaffold(
        containerColor = TolaBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TolaTextPrimary
                    )
                }

                Text(
                    text = "Report Found Item",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = TolaTextPrimary
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Did you find this item?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TolaTextPrimary
            )

            Text(
                text = "Provide details about where and when you found the item so its owner can be helped with recovery.",
                style = MaterialTheme.typography.bodyMedium,
                color = TolaTextSecondary
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = TolaSurface),
                border = BorderStroke(1.dp, TolaBorder),
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
                        text = itemName?.takeIf { it.isNotBlank() } ?: "Item ID: $itemId",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TolaTextPrimary
                    )

                    Text(
                        text = "This report will be linked to the existing item.",
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
                placeholder = { Text("e.g. University library") },
                singleLine = true,
                isError = locationError,
                supportingText = {
                    if (locationError) {
                        Text("Please enter where you found the item.", color = TolaError)
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

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dateFound,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Date found *") },
                    placeholder = { Text("Select date (DD/MM/YYYY)") },
                    singleLine = true,
                    isError = dateError,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.DateRange,
                                contentDescription = "Select date"
                            )
                        }
                    },
                    supportingText = {
                        if (dateError) {
                            Text("Please enter the date the item was found.", color = TolaError)
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
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = timeFound,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Time found (optional)") },
                    placeholder = { Text("Select time (e.g. 2:30 PM)") },
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Select time"
                            )
                        }
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TolaPrimary,
                        unfocusedBorderColor = TolaBorderStrong,
                        cursorColor = TolaPrimary
                    )
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showTimePicker = true }
                )
            }

            OutlinedTextField(
                value = additionalDetails,
                onValueChange = { additionalDetails = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Additional information") },
                placeholder = { Text("Describe the item's condition or where it is being kept") },
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TolaPrimary,
                    unfocusedBorderColor = TolaBorderStrong,
                    cursorColor = TolaPrimary
                )
            )

            // Photo Attachment
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        photoPickerLauncher.launch("image/*")
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedImageUri != null) {
                        TolaPrimary.copy(alpha = 0.08f)
                    } else {
                        TolaSurface
                    }
                ),
                border = BorderStroke(1.dp, TolaBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (selectedImageUri != null) Color(0xFF16A34A).copy(alpha = 0.15f) else TolaPrimary.copy(alpha = 0.1f),
                                RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (selectedImageUri != null) Icons.Default.CheckCircle else Icons.Default.AddAPhoto,
                            contentDescription = null,
                            tint = if (selectedImageUri != null) Color(0xFF16A34A) else TolaPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (selectedImageUri != null) "Photo attached" else "Add photo of found item",
                            fontWeight = FontWeight.SemiBold,
                            color = TolaTextPrimary
                        )

                        Text(
                            text = if (selectedImageUri != null) {
                                selectedImageUri?.lastPathSegment ?: "Image selected (click to change)"
                            } else {
                                "Optional (JPG/PNG)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = TolaTextMuted
                        )
                    }

                    if (selectedImageUri != null) {
                        IconButton(onClick = { selectedImageUri = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = TolaTextMuted
                            )
                        }
                    }
                }
            }

            Button(
                onClick = {
                    attemptedSubmit = true
                    if (locationFound.isNotBlank() && dateFound.isNotBlank()) {
                        onSubmitReport(
                            FoundItemReportData(
                                itemId = itemId,
                                locationFound = locationFound.trim(),
                                dateFound = dateFound.trim(),
                                timeFound = timeFound.trim(),
                                additionalDetails = additionalDetails.trim(),
                                imageUrl = selectedImageUri?.toString().orEmpty()
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
                    contentColor = Color.White
                )
            ) {
                Text("Submit Found Report", fontWeight = FontWeight.Bold, color = Color.White)
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, TolaBorderStrong)
            ) {
                Text("Cancel", color = TolaPrimary, fontWeight = FontWeight.SemiBold)
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
