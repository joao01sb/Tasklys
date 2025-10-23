package com.joao01sb.tasklys.features.notes.presentation.note

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joao01sb.tasklys.core.data.mock.MockData
import com.joao01sb.tasklys.core.domain.model.Note
import com.joao01sb.tasklys.core.domain.model.NoteFilter
import com.joao01sb.tasklys.core.domain.model.NoteStatus
import com.joao01sb.tasklys.core.utils.DateUtils.formatDate
import com.joao01sb.tasklys.core.theme.Background
import com.joao01sb.tasklys.core.theme.Error
import com.joao01sb.tasklys.core.theme.IconPrimary
import com.joao01sb.tasklys.core.theme.OnBackground
import com.joao01sb.tasklys.core.theme.OnSurface
import com.joao01sb.tasklys.core.theme.OnSurfaceVariant
import com.joao01sb.tasklys.core.theme.Outline
import com.joao01sb.tasklys.core.theme.Primary
import com.joao01sb.tasklys.core.theme.Secondary
import com.joao01sb.tasklys.core.theme.Success
import com.joao01sb.tasklys.core.theme.Surface


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    state: NoteUiState<List<Note>>,
    searchQuery: String,
    selectedFilter: NoteFilter,
    onSearchQueryChange: (String) -> Unit,
    onFilterChange: (NoteFilter) -> Unit,
    onNoteClick: (Note) -> Unit,
    onNoteComplete: (Note) -> Unit,
    onNoteDelete: (Note) -> Unit,
    onAddNoteClick: () -> Unit,
    onDeleteAllClick: () -> Unit
) {

    var showSearchBar by remember { mutableStateOf(false) }
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Notes",
                    fontWeight = FontWeight.Bold,
                    color = OnSurface
                )
            },
            actions = {
                IconButton(
                    onClick = { showSearchBar = !showSearchBar }
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = IconPrimary
                    )
                }
                IconButton(
                    onClick = { showDeleteAllDialog = true }
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete all",
                        tint = IconPrimary
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Surface
            )
        )

        AnimatedVisibility(
            visible = showSearchBar,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        AnimatedVisibility(
            visible = showDeleteAllDialog,
            enter = slideInVertically() + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            AlertDialog(
                onDismissRequest = { showDeleteAllDialog = false },
                title = { Text("Delete all notes?") },
                text = {
                    Text("This will permanently delete all your notes. This action cannot be undone.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onDeleteAllClick()
                            showDeleteAllDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete All")
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showDeleteAllDialog = false }
                    ) {
                        Text("Cancel")
                    }
                },
                containerColor = Background
            )
        }

        FilterChips(
            selectedFilter = selectedFilter,
            onFilterChange = onFilterChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            }
            !state.error.isNullOrEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.error, color = OnSurfaceVariant)
                }
            }
            !state.data.isNullOrEmpty() -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(
                        items = state.data,
                        key = { it.id }
                    ) { note ->
                        NoteCard(
                            note = note,
                            onClick = { onNoteClick(note) },
                            onComplete = { onNoteComplete(note) },
                            onDelete = { onNoteDelete(note) },
                            modifier = Modifier
                        )
                    }
                }
            }
            else -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Not found notes", color = OnSurfaceVariant)
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = onAddNoteClick,
            modifier = Modifier.padding(24.dp),
            containerColor = Primary,
            contentColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Add note"
            )
        }
    }
}

@Preview
@Composable
fun NoteListScreenPreview() {
    NoteListScreen(
        state = NoteUiState(data = MockData.mockNotes),
        searchQuery = "",
        selectedFilter = NoteFilter.ALL,
        onSearchQueryChange = {},
        onFilterChange = {},
        onNoteClick = {},
        onNoteComplete = {},
        onNoteDelete = {},
        onAddNoteClick = {},
        onDeleteAllClick = {}
    )
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        label = {
            Text(
                "Search notes",
                color = OnSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = IconPrimary
            )
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Primary,
            unfocusedBorderColor = Outline,
            focusedLabelColor = Primary,
            unfocusedLabelColor = OnSurfaceVariant,
            focusedTextColor = OnSurface,
            unfocusedTextColor = OnSurface
        )
    )
}

@Composable
private fun FilterChips(
    selectedFilter: NoteFilter,
    onFilterChange: (NoteFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(NoteFilter.entries.toTypedArray()) { filter ->
            FilterChip(
                selected = selectedFilter == filter,
                onClick = { onFilterChange(filter) },
                label = {
                    Text(
                        text = filter.displayName,
                        fontSize = 14.sp
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White,
                    containerColor = Secondary,
                    labelColor = OnSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpired = note.expiresAt?.let { it < System.currentTimeMillis() } == true
    val isCompleted = note.status == NoteStatus.COMPLETED

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            hoveredElevation = 4.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = note.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                        color = if (isCompleted) OnSurfaceVariant else OnSurface
                    )

                    if (note.content.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note.content,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            color = if (isCompleted) OnSurfaceVariant else OnSurfaceVariant
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onComplete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isCompleted)
                                Icons.Outlined.CheckCircle
                            else
                                Icons.Outlined.Notifications,
                            contentDescription = if (isCompleted) "Mark as pending" else "Mark as complete",
                            tint = if (isCompleted) OnSurfaceVariant else IconPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Create at ${formatDate(note.createdAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = OnBackground
                )

                note.expiresAt?.let { expiresAt ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            Icons.Default.DateRange,
                            contentDescription = "Expires",
                            modifier = Modifier.size(12.dp),
                            tint = if (isExpired) Error else Success
                        )
                        Text(
                            text = formatDate(expiresAt),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            color = if (isExpired) Error else Success
                        )
                    }
                }
            }
        }
    }
}