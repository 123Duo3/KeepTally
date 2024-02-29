package com.konyaco.keeptally.di

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.konyaco.keeptally.KeepTallyApplication
import com.konyaco.keeptally.api.KeepTallyApi
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.storage.database.Migration_1_2
import com.konyaco.keeptally.viewmodel.SharedViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal object InjectionModule {
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, "database"
        ).addMigrations(Migration_1_2).build()
    }

    @Provides
    fun provideApi(@ApplicationContext context: Context, dataStore: DataStore<Preferences>, database: AppDatabase): KeepTallyApi {
        return KeepTallyApi(database)
    }

    @Provides
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Composable
fun sharedViewModel(): SharedViewModel {
    val ctx = LocalContext.current.applicationContext
    return remember(ctx) {
        (ctx as KeepTallyApplication).sharedViewModel
    }
}