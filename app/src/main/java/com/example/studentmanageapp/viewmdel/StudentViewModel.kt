package com.example.studentmanageapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanageapp.data.database.AppDatabase
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.data.repository.StudentRepository
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.model.AttendanceStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val studentRepository = StudentRepository(db.studentDao(), db.attendanceDao())
    private val attendanceDao = db.attendanceDao()

    // ✅ 학생 리스트를 StateFlow로 관리
    private val _studentList = MutableStateFlow<List<Student>>(emptyList())
    val studentList: StateFlow<List<Student>> = _studentList

    init {
        loadStudents()
    }

    // ✅ 학생 전체 불러오기
    fun loadStudents() = viewModelScope.launch {
        val students = studentRepository.getAllStudents()
        _studentList.value = students
    }

    // ✅ 학생 추가 후 갱신
    fun addStudent(student: Student) = viewModelScope.launch {
        studentRepository.addStudent(student)
        loadStudents()
    }

    // ✅ 학생 수정 후 갱신
    fun updateStudent(student: Student) = viewModelScope.launch {
        studentRepository.updateStudent(student)

        // ✅ 기존 리스트에서 해당 학생만 교체
        val currentList = _studentList.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == student.id }
        if (index != -1) {
            currentList[index] = student
            _studentList.value = currentList // ✅ Compose가 변경을 감지함
        }
    }

    // ✅ 학생 삭제 후 갱신
    fun deleteStudent(student: Student) = viewModelScope.launch {
        studentRepository.deleteStudent(student)
        loadStudents()
    }

    // ✅ 학생 + 출석 기록 삭제 후 갱신
    fun deleteStudentWithAttendance(student: Student) = viewModelScope.launch {
        studentRepository.deleteStudentWithAttendance(student)
        loadStudents()
    }

    // ✅ 출석 기록 저장
    fun saveAttendance(record: AttendanceRecord) = viewModelScope.launch {
        attendanceDao.deleteRecord(record.studentId, record.date)
        attendanceDao.insert(record)
    }

    // ✅ 특정 날짜 출석 기록
    fun getAttendanceByDate(date: LocalDate, onResult: (List<AttendanceRecord>) -> Unit) = viewModelScope.launch {
        val records = attendanceDao.getRecordsByDate(date)
        onResult(records)
    }

    // ✅ 전체 출석 기록 (삭제된 학생 제외)
    fun getAllAttendanceDates(onResult: (List<AttendanceRecord>) -> Unit) = viewModelScope.launch {
        val validStudentIds = studentRepository.getAllStudents().map { it.id }.toSet()
        val records = attendanceDao.getAllRecords()
        val filtered = records.filter { it.studentId in validStudentIds }
        onResult(filtered)
    }

    // ✅ 월별 출석 통계
    fun getMonthlyAttendanceStats(
        yearMonth: YearMonth,
        onResult: (Map<Int, Map<AttendanceStatus, Int>>) -> Unit
    ) = viewModelScope.launch {
        val records = attendanceDao.getRecordsByMonth(yearMonth.toString())
        val filtered = records.filter { it.status != AttendanceStatus.PRESENT }
        val stats = filtered
            .groupBy { it.studentId }
            .mapValues { (_, list) ->
                list.groupingBy { it.status }.eachCount()
            }
        onResult(stats)
    }

    fun getAttendanceByMonth(yearMonth: String, callback: (List<AttendanceRecord>) -> Unit) {
        viewModelScope.launch {
            val records = attendanceDao.getRecordsByMonth(yearMonth)
            callback(records)
        }
    }

    fun getFilteredMonthlyAttendance(
        yearMonth: YearMonth,
        onResult: (List<AttendanceRecord>) -> Unit
    ) = viewModelScope.launch {
        val records = attendanceDao.getRecordsByMonth(yearMonth.toString())
        val filtered = records.filter { it.status != AttendanceStatus.PRESENT }
        onResult(filtered)
    }
}