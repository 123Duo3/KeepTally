package me.konyaco.keeptally.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.konyaco.keeptally.database.AppDatabase
import me.konyaco.keeptally.entity.Record
import me.konyaco.keeptally.entity.RecordType
import java.time.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlin.math.abs

@HiltViewModel
class DetailViewModel @Inject constructor(
    database: AppDatabase
) : ViewModel() {

}