package com.konyaco.keeptally.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.konyaco.keeptally.storage.database.AppDatabase
import com.konyaco.keeptally.viewmodel.model.Money
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val sharedViewModel: SharedViewModel,
    private val database: AppDatabase
) : ViewModel() {
    val state = mutableStateOf<MainViewModel.Companion.State>(MainViewModel.Companion.State.Initializing)
    val statistics = mutableStateOf<MainViewModel.Companion.Statistics>(
        MainViewModel.Companion.Statistics(
            Money(0),
            Money(0),
            Money(0)
        )
    )

    fun deleteRecord(recordId: Long) {

    }
}