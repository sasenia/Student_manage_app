package com.example.studentmanageapp.model

import java.time.LocalDate

data class AttendanceRecord(
    val studentId: Int,
    val studentName: String,
    val date: LocalDate,
    val status: String
)