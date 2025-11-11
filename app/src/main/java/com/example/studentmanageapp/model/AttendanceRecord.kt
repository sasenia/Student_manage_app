package com.example.studentmanageapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "attendance_records", primaryKeys = ["studentId", "date"])
data class AttendanceRecord(
    val studentId: Int,
    val studentName: String,
    val date: LocalDate,
    val status: AttendanceStatus
)