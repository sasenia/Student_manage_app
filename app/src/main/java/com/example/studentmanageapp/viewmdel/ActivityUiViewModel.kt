package com.example.studentmanageapp.viewmdel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class ActivityUiViewModel : ViewModel() {

    var uiState by mutableStateOf(ActivityUiState())
        private set

    fun selectActivity(name: String) {
        uiState = uiState.copy(selectedActivityName = name)
    }

    fun selectDate(date: LocalDate) {
        uiState = uiState.copy(selectedDate = date)
    }

    fun clearSelectedActivity() {
        uiState = uiState.copy(selectedActivityName = null)
    }
}
