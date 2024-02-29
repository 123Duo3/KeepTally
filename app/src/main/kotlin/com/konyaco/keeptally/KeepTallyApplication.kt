package com.konyaco.keeptally

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.konyaco.keeptally.api.KeepTallyApi
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.storage.database.Migration_1_2
import com.konyaco.keeptally.viewmodel.SharedViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject

@HiltAndroidApp
class KeepTallyApplication : Application() {
    @Inject
    lateinit var sharedViewModel: SharedViewModel

    @Inject
    lateinit var database: AppDatabase
}