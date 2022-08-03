package me.konyaco.keeptally.ui.statistic.expenditure

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import me.konyaco.keeptally.database.AppDatabase
import javax.inject.Inject

@HiltViewModel
class ExpenditureViewModel @Inject constructor(
    database: AppDatabase
) : ViewModel() {

}