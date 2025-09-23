package com.joao01sb.tasklys

import android.app.Application
import com.joao01sb.tasklys.features.notes.conatiner.AppContainer

class App : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

}