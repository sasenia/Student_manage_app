package com.example.studentmanageapp.viewmdel

import java.time.LocalDate

data class HomeworkUiState(
    val selectedSubjectName: String? = null,
    val selectedDate: LocalDate = LocalDate.now()
)

