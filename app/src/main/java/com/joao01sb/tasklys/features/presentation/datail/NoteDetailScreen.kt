package com.joao01sb.tasklys.features.notes.presentation.datail

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.joao01sb.tasklys.core.domain.model.DayOfWeek
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteStatus
import com.joao01sb.tasklys.core.domain.model.RecurrenceType
import com.joao01sb.tasklys.core.theme.Background
import com.joao01sb.tasklys.core.theme.Error
import com.joao01sb.tasklys.core.theme.IconPrimary
import com.joao01sb.tasklys.core.theme.OnBackground
import com.joao01sb.tasklys.core.theme.OnPrimary
import com.joao01sb.tasklys.core.theme.OnSurface
import com.joao01sb.tasklys.core.theme.OnSurfaceVariant
import com.joao01sb.tasklys.core.theme.Outline
import com.joao01sb.tasklys.core.theme.Primary
import com.joao01sb.tasklys.core.theme.Secondary
import com.joao01sb.tasklys.core.theme.Success
import com.joao01sb.tasklys.core.theme.Surface
import com.joao01sb.tasklys.core.theme.SurfaceVariant
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteDetailsScreen(
    detailUiState: NoteDetailUiState,
    onBackClick: () -> Unit,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onExpiryDateChange: (Long?) -> Unit,
    onDatePickerToggle: (Boolean) -> Unit,
    onSaveClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onCompleteToggle: () -> Unit,
    onEditToggle: () -> Unit,
    onRecurringExpandToggle: () -> Unit,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onDayToggle: (DayOfWeek) -> Unit
) {

    if (detailUiState.error != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Error loading note.",
                color = OnSurface,
                fontSize = 16.sp
            )
        }
        return
    } else if (detailUiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Background),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Primary)
        }
        return
    }

    val isNewNote = detailUiState.note.id == 0L
    val isCompleted = detailUiState.note.status == NoteStatus.COMPLETED
    val isExpired = detailUiState.note.expiresAt?.let { it < System.currentTimeMillis() } == true

    val titleFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isNewNote, detailUiState.isEditing) {
        if ((isNewNote || detailUiState.isEditing) && detailUiState.title.isEmpty()) {
            titleFocusRequester.requestFocus()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = when {
                        isNewNote -> "New Note"
                        detailUiState.isEditing -> "Editing"
                        else -> "Details"
                    },
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = IconPrimary
                    )
                }
            },
            actions = {
                if (!isNewNote) {
                    if (!detailUiState.isEditing) {
                        IconButton(onClick = onCompleteToggle) {
                            Icon(
                                imageVector = if (isCompleted)
                                    Icons.Outlined.CheckCircle
                                else
                                    Icons.Outlined.Notifications,
                                contentDescription = if (isCompleted)
                                    "Mark as pending"
                                else
                                    "Mark as completed",
                                tint = if (isCompleted) OnSurfaceVariant else IconPrimary
                            )
                        }

                        IconButton(onClick = onDeleteClick) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Error
                            )
                        }
                    }

                    Button(
                        onClick = {
                            if (detailUiState.isEditing) {
                                onSaveClick()
                                keyboardController?.hide()
                            } else {
                                onEditToggle()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary
                        ),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        if (detailUiState.isEditing) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (detailUiState.isEditing) "Save" else "Edit",
                            fontSize = 14.sp
                        )
                    }
                } else {
                    Button(
                        onClick = onSaveClick,
                        enabled = detailUiState.title.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = OnPrimary,
                            disabledContainerColor = Secondary,
                            disabledContentColor = OnSurfaceVariant
                        ),
                        shape = RoundedCornerShape(24.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save", fontSize = 14.sp)
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Surface
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            if (!isNewNote) {
                StatusIndicator(
                    isCompleted = isCompleted,
                    isExpired = isExpired,
                    createdAt = detailUiState.note.createdAt,
                    expiresAt = detailUiState.note.expiresAt
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (detailUiState.isEditing || isNewNote) {
                    Text(
                        text = "Title",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
                }

                if (detailUiState.isEditing || isNewNote) {
                    OutlinedTextField(
                        value = detailUiState.title,
                        onValueChange = onTitleChange,
                        placeholder = {
                            Text(
                                "Enter note title...",
                                color = OnBackground
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(titleFocusRequester),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Outline,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary,
                            focusedContainerColor = Surface,
                            unfocusedContainerColor = Surface
                        ),
                        singleLine = true
                    )
                } else {
                    Text(
                        text = detailUiState.title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = OnSurface
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (detailUiState.isEditing || isNewNote) {
                    Text(
                        text = "Content",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = OnSurface
                    )
                }

                if (detailUiState.isEditing || isNewNote) {
                    OutlinedTextField(
                        value = detailUiState.content,
                        onValueChange = onContentChange,
                        placeholder = {
                            Text(
                                "Enter note content...",
                                color = OnBackground
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 150.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Primary,
                            unfocusedBorderColor = Outline,
                            focusedTextColor = OnSurface,
                            unfocusedTextColor = OnSurface,
                            cursorColor = Primary,
                            focusedContainerColor = Surface,
                            unfocusedContainerColor = Surface
                        ),
                        maxLines = 10
                    )
                } else if (detailUiState.content.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = SurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Outline)
                    ) {
                        Text(
                            text = detailUiState.content,
                            fontSize = 15.sp,
                            color = OnSurfaceVariant,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            if (detailUiState.isEditing || isNewNote) {
                ExpiryDateSection(
                    expiryDate = detailUiState.expiryDate,
                    onDateClick = { onDatePickerToggle(true) },
                    onRemoveDate = { onExpiryDateChange(null) }
                )
            }

            if (detailUiState.isEditing || isNewNote) {
                RecurringNotificationSection(
                    isExpanded = detailUiState.isRecurringExpanded,
                    recurrenceType = detailUiState.recurrenceType,
                    selectedDays = detailUiState.selectedDays,
                    onExpandToggle = onRecurringExpandToggle,
                    onRecurrenceTypeChange = onRecurrenceTypeChange,
                    onDayToggle = onDayToggle
                )
            }

            if (isNewNote) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onSaveClick,
                    enabled = detailUiState.title.trim().isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Primary,
                        contentColor = OnPrimary,
                        disabledContainerColor = Secondary,
                        disabledContentColor = OnSurfaceVariant
                    ),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    Text(
                        text = "Create Note",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (detailUiState.showDatePicker) {
        DateTimePickerDialog(
            onDateTimeSelected = { selectedDate ->
                onExpiryDateChange(selectedDate)
                onDatePickerToggle(false)
            },
            onDismiss = { onDatePickerToggle(false) }
        )
    }
}

@Preview
@Composable
private fun NoteDetailsScreenPreview() {
    NoteDetailsScreen(
        detailUiState = NoteDetailUiState(
            note = Note(
                id = 1L,
                title = "Sample Note",
                content = "This is a sample note content.",
                status = NoteStatus.ACTIVE,
                createdAt = System.currentTimeMillis() - 3600000,
                expiresAt = System.currentTimeMillis() + 86400000
            ),
            title = "Sample Note",
            content = "This is a sample note content.",
            expiryDate = System.currentTimeMillis() + 86400000,
            isEditing = true,
            isLoading = false
        ),
        onBackClick = {},
        onTitleChange = {},
        onContentChange = {},
        onExpiryDateChange = {},
        onDatePickerToggle = {},
        onSaveClick = {},
        onDeleteClick = {},
        onCompleteToggle = {},
        onEditToggle = {},
        onRecurringExpandToggle = {},
        onRecurrenceTypeChange = {},
        onDayToggle = {}
    )
}

@Composable
private fun StatusIndicator(
    isCompleted: Boolean,
    isExpired: Boolean,
    createdAt: Long,
    expiresAt: Long?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isCompleted -> Success.copy(alpha = 0.1f)
                isExpired -> Error.copy(alpha = 0.1f)
                else -> SurfaceVariant
            }
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            when {
                isCompleted -> Success.copy(alpha = 0.3f)
                isExpired -> Error.copy(alpha = 0.3f)
                else -> Outline
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = when {
                                isCompleted -> Success
                                isExpired -> Error
                                else -> OnBackground
                            },
                            shape = CircleShape
                        )
                )
                Text(
                    text = when {
                        isCompleted -> "Completed"
                        isExpired -> "Expired"
                        else -> "Pending"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = when {
                        isCompleted -> Success
                        isExpired -> Error
                        else -> OnSurface
                    }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Created: ${formatDateTime(createdAt)}",
                    fontSize = 12.sp,
                    color = OnSurfaceVariant
                )

                expiresAt?.let {
                    Text(
                        text = "Expires: ${formatDateTime(it)}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = if (isExpired) Error else Success
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpiryDateSection(
    expiryDate: Long?,
    onDateClick: () -> Unit,
    onRemoveDate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Expiration Date",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = OnSurface
            )

            if (expiryDate != null) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = IconPrimary
                        )
                        Text(
                            text = formatDateTime(expiryDate),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = OnSurface
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = OnBackground
                        )
                        Text(
                            text = getTimeUntilExpiry(expiryDate),
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        TextButton(
                            onClick = onDateClick,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = OnSurfaceVariant
                            )
                        ) {
                            Text("Change", fontSize = 13.sp)
                        }
                        TextButton(
                            onClick = onRemoveDate,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = Error
                            )
                        ) {
                            Text("Remove", fontSize = 13.sp)
                        }
                    }
                }
            } else {
                Button(
                    onClick = onDateClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Surface,
                        contentColor = OnSurfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Outline),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Set Deadline", fontSize = 14.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateTimePickerDialog(
    onDateTimeSelected: (Long) -> Unit,
    onDismiss: () -> Unit
) {

    val brTimeZone = TimeZone.getTimeZone("America/Sao_Paulo")

    val calendar = Calendar.getInstance(brTimeZone).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = calendar.timeInMillis
    )

    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableIntStateOf(23) }
    var selectedMinute by remember { mutableIntStateOf(59) }

    if (!showTimePicker) {
        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        if (datePickerState.selectedDateMillis != null) {
                            showTimePicker = true
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Text("Next")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = OnSurfaceVariant
                    )
                ) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Surface,
                    selectedDayContainerColor = Primary,
                    todayContentColor = Primary,
                    todayDateBorderColor = Primary
                )
            )
        }
    } else {
        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { dateMillis ->
                            val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                                timeInMillis = dateMillis
                            }

                            val brCalendar = Calendar.getInstance(brTimeZone).apply {
                                set(Calendar.YEAR, utcCalendar.get(Calendar.YEAR))
                                set(Calendar.MONTH, utcCalendar.get(Calendar.MONTH))
                                set(Calendar.DAY_OF_MONTH, utcCalendar.get(Calendar.DAY_OF_MONTH))
                                set(Calendar.HOUR_OF_DAY, selectedHour)
                                set(Calendar.MINUTE, selectedMinute)
                                set(Calendar.SECOND, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            onDateTimeSelected(brCalendar.timeInMillis)
                        }
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Primary
                    )
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showTimePicker = false },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = OnSurfaceVariant
                    )
                ) {
                    Text("Back")
                }
            },
            title = {
                Text("Select Time")
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        NumberPicker(
                            value = selectedHour,
                            onValueChange = { selectedHour = it },
                            range = 0..23,
                            label = "Hour"
                        )

                        Text(
                            text = ":",
                            style = MaterialTheme.typography.headlineLarge,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        NumberPicker(
                            value = selectedMinute,
                            onValueChange = { selectedMinute = it },
                            range = 0..59,
                            label = "Minute"
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    label: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                if (value < range.last) onValueChange(value + 1)
                else onValueChange(range.first)
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Increase"
            )
        }

        Text(
            text = String.format("%02d", value),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        IconButton(
            onClick = {
                if (value > range.first) onValueChange(value - 1)
                else onValueChange(range.last)
            }
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Decrease"
            )
        }

        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant
        )
    }
}

@Composable
private fun RecurringNotificationSection(
    isExpanded: Boolean,
    recurrenceType: RecurrenceType,
    selectedDays: Set<DayOfWeek>,
    onExpandToggle: () -> Unit,
    onRecurrenceTypeChange: (RecurrenceType) -> Unit,
    onDayToggle: (DayOfWeek) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariant
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Outline)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onExpandToggle)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "zzz",
                        fontSize = 24.sp
                    )
                    Column {
                        Text(
                            text = "Recurring Notification",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = OnSurface
                        )
                        Text(
                            text = when (recurrenceType) {
                                RecurrenceType.ONCE -> "One time only"
                                RecurrenceType.DAILY -> "Every day"
                                RecurrenceType.WEEKDAYS -> "Weekdays (Mon-Fri)"
                                RecurrenceType.WEEKEND -> "Weekends"
                                RecurrenceType.CUSTOM -> if (selectedDays.isNotEmpty())
                                    "${selectedDays.size} days selected"
                                else
                                    "Not configured"
                            },
                            fontSize = 12.sp,
                            color = OnSurfaceVariant
                        )
                    }
                }

                Icon(
                    imageVector = if (isExpanded)
                        Icons.Default.KeyboardArrowUp
                    else
                        Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = OnSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(
                        modifier = Modifier.padding(bottom = 4.dp),
                        color = Outline
                    )

                    RecurrenceOption(
                        selected = recurrenceType == RecurrenceType.ONCE,
                        label = "One time only",
                        onClick = { onRecurrenceTypeChange(RecurrenceType.ONCE) }
                    )

                    RecurrenceOption(
                        selected = recurrenceType == RecurrenceType.DAILY,
                        label = "Every day",
                        onClick = { onRecurrenceTypeChange(RecurrenceType.DAILY) }
                    )

                    RecurrenceOption(
                        selected = recurrenceType == RecurrenceType.WEEKDAYS,
                        label = "Weekdays (Mon-Fri)",
                        onClick = { onRecurrenceTypeChange(RecurrenceType.WEEKDAYS) }
                    )

                    RecurrenceOption(
                        selected = recurrenceType == RecurrenceType.WEEKEND,
                        label = "Weekends",
                        onClick = { onRecurrenceTypeChange(RecurrenceType.WEEKEND) }
                    )

                    RecurrenceOption(
                        selected = recurrenceType == RecurrenceType.CUSTOM,
                        label = "Custom days",
                        onClick = { onRecurrenceTypeChange(RecurrenceType.CUSTOM) }
                    )

                    AnimatedVisibility(visible = recurrenceType == RecurrenceType.CUSTOM) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp, top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DayOfWeek.entries.forEach { day ->
                                DayButton(
                                    day = day,
                                    isSelected = selectedDays.contains(day),
                                    onClick = { onDayToggle(day) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecurrenceOption(
    selected: Boolean,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = selected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(
                selectedColor = Primary,
                unselectedColor = OnSurfaceVariant
            )
        )
        Text(
            text = label,
            fontSize = 14.sp,
            color = OnSurface
        )
    }
}

@Composable
private fun DayButton(
    day: DayOfWeek,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dayLabel = when (day) {
        DayOfWeek.SUNDAY -> "D"
        DayOfWeek.MONDAY -> "S"
        DayOfWeek.TUESDAY -> "T"
        DayOfWeek.WEDNESDAY -> "Q"
        DayOfWeek.THURSDAY -> "Q"
        DayOfWeek.FRIDAY -> "S"
        DayOfWeek.SATURDAY -> "S"
    }

    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                color = if (isSelected) Primary else Surface,
                shape = CircleShape
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Primary else Outline,
                shape = CircleShape
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = dayLabel,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) OnPrimary else OnSurface
        )
    }
}

private fun formatDateTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

private fun getTimeUntilExpiry(expiryDate: Long): String {
    val now = System.currentTimeMillis()
    val diff = expiryDate - now

    return when {
        diff < 0 -> "Expired"
        diff < 24 * 60 * 60 * 1000 -> {
            val hours = (diff / (60 * 60 * 1000)).toInt()
            "$hours hours left"
        }
        diff < 7 * 24 * 60 * 60 * 1000 -> {
            val days = (diff / (24 * 60 * 60 * 1000)).toInt()
            "$days days left"
        }
        else -> {
            val weeks = (diff / (7 * 24 * 60 * 60 * 1000)).toInt()
            "$weeks weeks left"
        }
    }
}