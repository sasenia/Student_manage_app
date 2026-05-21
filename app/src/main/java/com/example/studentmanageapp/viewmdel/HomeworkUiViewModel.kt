package com.example.studentmanageapp.viewmdel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.time.LocalDate

class HomeworkUiViewModel : ViewModel() {

    var uiState by mutableStateOf(HomeworkUiState())
        private set

    fun selectDate(date: LocalDate) {
        uiState = uiState.copy(selectedDate = date)
    }

    fun selectSubject(name: String?) {
        uiState = uiState.copy(selectedSubjectName = name)
    }
}
