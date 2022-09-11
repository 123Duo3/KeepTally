package me.konyaco.keeptally.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.konyaco.keeptally.storage.database.AppDatabase
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    database: AppDatabase
) : ViewModel() {

}