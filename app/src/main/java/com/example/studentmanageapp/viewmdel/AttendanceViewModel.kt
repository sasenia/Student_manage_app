package com.example.studentmanageapp.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.YearMonth

data class Student(
    val id: Int,
    val name: String
)

data class AttendanceRecord(
    val studentId: Int,
    val studentName: String,
    val date: LocalDate,
    val status: String // "출석", "결석", "지각", "조퇴"
)

class AttendanceViewModel : ViewModel() {
    val students = mutableStateListOf<Student>()
    private val _records = mutableStateListOf<AttendanceRecord>()
    val records: List<AttendanceRecord> get() = _records

    fun addStudent(name: String) {
        val newId = (students.maxOfOrNull { it.id } ?: 0) + 1
        students.add(Student(newId, name))
    }

    fun markAttendance(studentId: Int, date: LocalDate, status: String) {
        val student = students.find { it.id == studentId } ?: return
        _records.removeAll { it.studentId == studentId && it.date == date }
        _records.add(AttendanceRecord(student.id, student.name, date, status))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getMonthlyRecords(month: YearMonth): List<AttendanceRecord> {
        return _records.filter { YearMonth.from(it.date) == month && it.status != "출석" }
    }
    //수정시 오류 확인
}