package com.joao01sb.tasklys

import android.app.Application
import com.joao01sb.tasklys.core.data.local.NoteDatabase
import com.joao01sb.tasklys.features.notes.conatiner.AppContainer

class App : Application() {

    lateinit var container: AppContainer
        private set

    lateinit var database: NoteDatabase
        private set


    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
        database = NoteDatabase.getDatabase(this)
    }

}