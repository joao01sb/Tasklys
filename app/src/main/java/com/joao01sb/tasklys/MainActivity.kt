package com.joao01sb.tasklys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.joao01sb.tasklys.features.notes.presentation.NoteListScreen
import com.joao01sb.tasklys.features.notes.presentation.NoteScreen
import com.joao01sb.tasklys.features.notes.presentation.NoteViewModel
import com.joao01sb.tasklys.features.notes.presentation.NoteViewModelFactory
import com.joao01sb.tasklys.ui.theme.TasklysTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.joao01sb.tasklys.features.notes.presentation.NoteDetailsScreen


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

    val viewmodel: NoteViewModel = viewModel(
        factory = NoteViewModelFactory(
            addNote = appContainer.addNote,
            allNotes = appContainer.allNotes,
            deleteAllNotes = appContainer.deleteAllNotes,
            deleteNote = appContainer.deleteNote,
            getNoteById = appContainer.getNoteById,
            updateNote = appContainer.updateNote,
            getNotesByFilter = appContainer.getNotesByFilter
        )
    )

    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()

    when (viewmodel.currentScreen) {
        NoteScreen.LIST -> NoteListScreen(
            state = uiState,
            onNoteClick = {},
            onAddNoteClick = {},
            searchQuery = viewmodel.currentQuery,
            selectedFilter = viewmodel.selectedFilter,
            onSearchQueryChange = {
                viewmodel.onQueryChange(it)
            },
            onFilterChange = {
                viewmodel.updateSelectedFilter(it)
            },
            onNoteComplete = {

            },
            onNoteDelete = {

            },
            onDeleteAllClick = {

            }
        )
        NoteScreen.DETAIL -> {
            NoteDetailsScreen() 
        }
    }

}