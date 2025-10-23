package com.joao01sb.tasklys.features.notes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joao01sb.tasklys.App
import com.joao01sb.tasklys.features.notes.presentation.datail.NoteDetailsScreen
import com.joao01sb.tasklys.features.notes.presentation.note.NoteListScreen
import com.joao01sb.tasklys.features.notes.presentation.NoteScreen
import com.joao01sb.tasklys.features.notes.presentation.NoteViewModel
import com.joao01sb.tasklys.features.notes.presentation.NoteViewModelFactory
import com.joao01sb.tasklys.core.theme.TasklysTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TasklysTheme {
                AppContainer()
            }
        }
    }
}

@Composable
fun AppContainer() {

    val context = LocalContext.current
    val appContainer = (context.applicationContext as App).container

    val viewModel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            addNote = appContainer.addNote,
            allNotes = appContainer.allNotes,
            deleteAllNotes = appContainer.deleteAllNotes,
            deleteNote = appContainer.deleteNote,
            getNoteById = appContainer.getNoteById,
            updateNote = appContainer.updateNote
        )
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val detailUiState by viewModel.detailUiState.collectAsStateWithLifecycle()
    val filter by viewModel.selectedFilter.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->

        }
    }

    when (viewModel.currentScreen) {
        NoteScreen.LIST -> {
            NoteListScreen(
                state = uiState,
                searchQuery = viewModel.currentQuery,
                selectedFilter = filter,
                onSearchQueryChange = { query ->
                    viewModel.onQueryChange(query)
                    viewModel.searchNotes(query)
                },
                onFilterChange = { filter ->
                    viewModel.updateSelectedFilter(filter)
                },
                onNoteClick = { note ->
                    viewModel.navigateToDetail(note.id)
                },
                onNoteComplete = { note ->
                    viewModel.toggleCompleteNoteFromList(note)
                },
                onNoteDelete = { note ->
                    viewModel.handleDeleteNoteFromList(note)
                },
                onAddNoteClick = {
                    viewModel.navigateToDetail(null)
                },
                onDeleteAllClick = {
                    viewModel.handleDeleteAllNotes()
                }
            )
        }

        NoteScreen.DETAIL -> {
            NoteDetailsScreen(
                detailUiState = detailUiState,
                onBackClick = {
                    viewModel.navigateBackToList()
                },
                onTitleChange = { title ->
                    viewModel.updateTitle(title)
                },
                onContentChange = { content ->
                    viewModel.updateContent(content)
                },
                onExpiryDateChange = { date ->
                    viewModel.updateExpiryDate(date)
                },
                onDatePickerToggle = { show ->
                    viewModel.toggleDatePicker(show)
                },
                onSaveClick = {
                    if (detailUiState.note.id == 0L) {
                        viewModel.createNote()
                    } else {
                        viewModel.saveNote()
                    }
                },
                onDeleteClick = {
                    viewModel.handleDeleteNote()
                },
                onCompleteToggle = {
                    viewModel.toggleCompleteNote()
                },
                onEditToggle = {
                    viewModel.toggleEditing(!detailUiState.isEditing)
                }
            )
        }
    }
}