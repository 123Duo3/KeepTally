package com.konyaco.keeptally

import android.app.Application
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.viewmodel.SharedViewModel
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class KeepTallyApplication : Application() {
    @Inject
    lateinit var sharedViewModel: SharedViewModel

    @Inject
    lateinit var database: AppDatabase
}