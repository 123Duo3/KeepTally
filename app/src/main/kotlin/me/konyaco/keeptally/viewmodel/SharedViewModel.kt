package me.konyaco.keeptally.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedViewModel @Inject constructor() : ViewModel() {
    val dateRange = MutableStateFlow<MainViewModel.DateRange>(MainViewModel.DateRange.Month.now())
}