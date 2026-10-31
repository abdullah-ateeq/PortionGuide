package com.example.portionguide

import android.app.Application
import com.example.portionguide.data.database.PortionDatabase
import com.example.portionguide.data.preferences.AppPreferencesRepository
import com.example.portionguide.data.repository.PortionRepository
import com.example.portionguide.data.repository.PortionRepositoryImpl

class PortionGuideApplication : Application() {

    lateinit var database: PortionDatabase
        private set

    lateinit var repository: PortionRepository
        private set

    lateinit var preferencesRepository: AppPreferencesRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = PortionDatabase.getInstance(this)
        repository = PortionRepositoryImpl(database.calculationDao())
        preferencesRepository = AppPreferencesRepository(this)
    }
}
