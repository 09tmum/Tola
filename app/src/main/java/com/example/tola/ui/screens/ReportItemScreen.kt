package com.example.tola.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.Surface
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.CircularProgressIndicator
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import com.example.tola.data.FirebaseStorageRepository
import com.example.tola.ui.theme.TolaFoundText
import com.example.tola.ui.theme.TolaLostText
import com.example.tola.ui.theme.TolaPrimary
import com.example.tola.ui.theme.TolaTheme
import com.example.tola.data.ReportRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportItemScreen(
    initialIsLost: Boolean = true,
    onNavigateBack: () -> Unit
) {
    var isLost by remember(initialIsLost) { mutableStateOf(initialIsLost) }

    var itemName by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("")
    }

    var location by remember {
        mutableStateOf("")
    }

    var date by remember {
        mutableStateOf("")
    }

    var showDatePicker by remember {
        mutableStateOf(false)
    }

    var time by remember {
        mutableStateOf("")
    }

    var showTimePicker by remember {
        mutableStateOf(false)
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val reportRepository = remember { ReportRepository() }
    val coroutineScope = rememberCoroutineScope()
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

    var isSubmitting by remember { mutableStateOf(false) }

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
                            date = formatter.format(Date(millis))
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
                        time = timeFormatter.format(cal.time)
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

    val categories = listOf(
        "Electronics",
        "Bags",
        "IDs",
        "Books",
        "Clothing",
        "Accessories",
        "Other"
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Report Item",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp)
        ) {

            Text(
                text = "Tell us about the item",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Lost / Found selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                StatusButton(
                    text = "I Lost It",
                    selected = isLost,
                    color = TolaLostText,
                    modifier = Modifier.weight(1f)
                ) {
                    isLost = true
                }

                StatusButton(
                    text = "I Found It",
                    selected = !isLost,
                    color = TolaFoundText,
                    modifier = Modifier.weight(1f)
                ) {
                    isLost = false
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Item name
            Text(
                text = "Item Name",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(7.dp))

            OutlinedTextField(
                value = itemName,
                onValueChange = {
                    itemName = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("e.g. Black backpack")
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Category
            Text(
                text = "Category",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(7.dp))

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = {
                    categoryExpanded = !categoryExpanded
                }
            ) {

                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    placeholder = {
                        Text("Select category")
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = categoryExpanded
                        )
                    },
                    shape = RoundedCornerShape(12.dp)
                )

                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = {
                        categoryExpanded = false
                    }
                ) {
                    categories.forEach { option ->

                        DropdownMenuItem(
                            text = {
                                Text(option)
                            },
                            onClick = {
                                category = option
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Description
            Text(
                text = "Description",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(7.dp))

            OutlinedTextField(
                value = description,
                onValueChange = {
                    if (it.length <= 300) {
                        description = it
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = {
                    Text(
                        "Describe the item, colour, markings, etc."
                    )
                },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Location
            Text(
                text = if (isLost) {
                    "Where did you lose it?"
                } else {
                    "Where did you find it?"
                },
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(7.dp))

            OutlinedTextField(
                value = location,
                onValueChange = {
                    location = it
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("e.g. Main Library")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date
            Text(
                text = "Date",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = date,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Select date (DD/MM/YYYY)")
                    },
                    leadingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = "Select date"
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time
            Text(
                text = "Time (optional)",
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(7.dp))

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Select time (e.g. 2:30 PM)")
                    },
                    leadingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = "Select time"
                            )
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showTimePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Photo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        photoPickerLauncher.launch("image/*")
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedImageUri != null) {
                        TolaPrimary.copy(alpha = 0.08f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (selectedImageUri != null) Color(0xFF16A34A).copy(alpha = 0.15f) else TolaPrimary.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
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
                            text = if (selectedImageUri != null) "Photo attached" else "Add a photo",
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = if (selectedImageUri != null) {
                                selectedImageUri?.lastPathSegment ?: "Image file selected (click to change)"
                            } else {
                                "Optional (JPG/PNG only)"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (selectedImageUri != null) {
                        IconButton(onClick = { selectedImageUri = null }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Remove photo",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy notice
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Your contact information will not be publicly displayed. "
                            + "It will only be shared when necessary to help return an item.",
                    modifier = Modifier.padding(14.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val isFormValid = itemName.isNotBlank() &&
                        category.isNotBlank() &&
                        location.isNotBlank() &&
                        date.isNotBlank()

            // Submit

            Button(
                onClick = {
                    if (!isFormValid || isSubmitting) return@Button

                    coroutineScope.launch {
                        isSubmitting = true

                        try {
                            val storageRepository = FirebaseStorageRepository()
                            val cloudImageUrl = if (selectedImageUri != null) {
                                storageRepository.uploadImage(context, selectedImageUri!!, "reports")
                            } else ""

                            reportRepository.submitReport(
                                itemName = itemName,
                                category = category,
                                description = description,
                                location = location,
                                date = date,
                                time = time,
                                isLost = isLost,
                                imageUrl = cloudImageUrl
                            )

                            Toast.makeText(
                                context,
                                if (isLost) {
                                    "Lost item reported successfully."
                                } else {
                                    "Found item reported successfully."
                                },
                                Toast.LENGTH_LONG
                            ).show()

                            onNavigateBack()
                        } catch (exception: Exception) {
                            Toast.makeText(
                                context,
                                "Could not submit report: ${
                                    exception.localizedMessage
                                        ?: "Please try again."
                                }",
                                Toast.LENGTH_LONG
                            ).show()
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                enabled = isFormValid && !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TolaPrimary,
                    contentColor = Color.White
                )
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(22.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = if (isLost) {
                            "Report Lost Item"
                        } else {
                            "Report Found Item"
                        },
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }


            Spacer(modifier = Modifier.height(10.dp))

            // Cancel
            Text(
                text = "Cancel",
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onNavigateBack()
                    }
                    .padding(12.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                color = TolaPrimary,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun StatusButton(
    text: String,
    selected: Boolean,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        modifier = modifier
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = if (selected) {
            color
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        }
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = if (selected) {
                    Color.White
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReportItemScreenPreview() {
    TolaTheme {
        ReportItemScreen(onNavigateBack = {})
    }
}