package com.example.studentmanageapp.viewmdel

import java.time.LocalDate

data class ActivityUiState(
    val selectedActivityName: String? = null,
    val selectedDate: LocalDate = LocalDate.now()
)
