package com.example.studentmanageapp.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanageapp.data.database.AppDatabase
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.data.repository.StudentRepository
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.model.AttendanceStatus
import com.example.studentmanageapp.network.ActivityRequest
import com.example.studentmanageapp.network.AttendanceRequest
import com.example.studentmanageapp.network.AttendanceResponse
import com.example.studentmanageapp.network.HomeworkRequest
import com.example.studentmanageapp.network.RetrofitClient
import com.example.studentmanageapp.network.ScoreRequest
import com.example.studentmanageapp.network.StudentRequest
import com.example.studentmanageapp.network.StudentResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val studentRepository = StudentRepository(db.studentDao(), db.attendanceDao())
    private val attendanceDao = db.attendanceDao()

    private val _studentList = MutableStateFlow<List<Student>>(emptyList())
    val studentList: StateFlow<List<Student>> = _studentList

    private val _serverStudents = MutableStateFlow<List<StudentResponse>>(emptyList())
    val serverStudents: StateFlow<List<StudentResponse>> = _serverStudents

    private val _serverMessage = MutableStateFlow("Server not called yet")
    val serverMessage: StateFlow<String> = _serverMessage

    init {
        loadStudentsFromServer()
    }

    fun loadStudents() = viewModelScope.launch {
        val students = studentRepository.getAllStudents()
        _studentList.value = students
    }

    fun addStudent(student: Student) = viewModelScope.launch {
        try {
            RetrofitClient.studentApi.addStudent(student.toRequest())
            loadStudentsFromServer()
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    fun updateStudent(student: Student) = viewModelScope.launch {
        try {
            RetrofitClient.studentApi.updateStudent(student.id, student.toRequest())
            loadStudentsFromServer()
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    fun deleteStudent(student: Student) = viewModelScope.launch {
        try {
            RetrofitClient.studentApi.deleteStudent(student.id)
            loadStudentsFromServer()
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    fun deleteStudentWithAttendance(student: Student) = viewModelScope.launch {
        studentRepository.deleteStudentWithAttendance(student)
        loadStudents()
    }

    fun saveAttendance(record: AttendanceRecord) = viewModelScope.launch {
        try {
            RetrofitClient.studentApi.saveAttendance(
                AttendanceRequest(
                    studentId = record.studentId,
                    attendanceStatus = record.status.name,
                    date = record.date.toString()
                )
            )
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    fun getAttendanceByDate(date: LocalDate, onResult: (List<AttendanceRecord>) -> Unit) = viewModelScope.launch {
        try {
            val records = RetrofitClient.studentApi
                .getAttendances(YearMonth.from(date).toString())
                .filter { it.date == date.toString() }
                .map { it.toAttendanceRecord() }
            onResult(records)
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
            onResult(emptyList())
        }
    }

    fun getAllAttendanceDates(onResult: (List<AttendanceRecord>) -> Unit) = viewModelScope.launch {
        val validStudentIds = studentRepository.getAllStudents().map { it.id }.toSet()
        val records = attendanceDao.getAllRecords()
        val filtered = records.filter { it.studentId in validStudentIds }
        onResult(filtered)
    }

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
            try {
                val records = RetrofitClient.studentApi
                    .getAttendances(yearMonth)
                    .map { it.toAttendanceRecord() }
                callback(records)
            } catch (e: Exception) {
                _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
                callback(emptyList())
            }
        }
    }

    fun getFilteredMonthlyAttendance(
        yearMonth: YearMonth,
        onResult: (List<AttendanceRecord>) -> Unit
    ) = viewModelScope.launch {
        try {
            val records = RetrofitClient.studentApi
                .getAttendances(yearMonth.toString())
                .map { it.toAttendanceRecord() }
            val filtered = records.filter { it.status != AttendanceStatus.PRESENT }
            onResult(filtered)
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
            onResult(emptyList())
        }
    }

    fun deleteAttendanceByDate(date: LocalDate, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                RetrofitClient.studentApi.deleteAttendanceByDate(date.toString())
            } catch (e: Exception) {
                _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
            }
            onDone()
        }
    }

    var lastUpdatedStudentId by mutableStateOf<Int?>(null)
        private set

    fun markStudentUpdated(studentId: Int) {
        lastUpdatedStudentId = studentId
    }

    fun clearLastUpdated() {
        lastUpdatedStudentId = null
    }

    fun loadStudentsFromServer() = viewModelScope.launch {
        _serverMessage.value = "Loading..."

        try {
            val students = RetrofitClient.studentApi.getStudents()
            val praises = RetrofitClient.studentApi.getPraises()
            val presentations = RetrofitClient.studentApi.getPresentations()
            val homeworks = RetrofitClient.studentApi.getHomeworks()
            val activities = RetrofitClient.studentApi.getActivities()

            val praiseMap = praises.associateBy { it.studentId }
            val presentationMap = presentations.associateBy { it.studentId }
            val homeworkMap = homeworks
                .groupBy { it.studentId }
                .mapValues { (_, records) ->
                    records
                        .groupBy { it.homeworkName }
                        .mapValues { (_, subjectRecords) ->
                            subjectRecords.associate { it.date to (it.homeworkScore ?: "") }
                        }
                }
            val homeworkMemoMap = homeworks
                .groupBy { it.studentId }
                .mapValues { (_, records) ->
                    records
                        .groupBy { it.homeworkName }
                        .mapValues { (_, subjectRecords) ->
                            subjectRecords.associate { it.date to (it.homeworkMemo ?: "") }
                        }
                }
            val activityMap = activities
                .groupBy { it.studentId }
                .mapValues { (_, records) ->
                    records.associate { "${it.activityName}-${it.date}" to it.activityScore }
                }

            _serverStudents.value = students
            _studentList.value = students.map { student ->
                student.toStudent().copy(
                    praiseScore = praiseMap[student.id]?.score ?: 0,
                    presentationScore = presentationMap[student.id]?.score ?: 0,
                    homeworkMap = homeworkMap[student.id] ?: emptyMap(),
                    memoMap = homeworkMemoMap[student.id] ?: emptyMap(),
                    activityMap = activityMap[student.id] ?: emptyMap()
                )
            }
            _serverMessage.value = "Success: ${students.size} students"
        } catch (e: Exception) {
            _serverStudents.value = emptyList()
            loadStudents()
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    private fun StudentResponse.toStudent(): Student {
        return Student(
            id = id,
            name = name,
            gender = gender,
            memo = memo ?: ""
        )
    }

    private fun Student.toRequest(): StudentRequest {
        return StudentRequest(
            id = id,
            name = name,
            gender = gender,
            memo = memo
        )
    }

    private fun AttendanceResponse.toAttendanceRecord(): AttendanceRecord {
        return AttendanceRecord(
            studentId = studentId,
            studentName = name,
            date = LocalDate.parse(date),
            status = AttendanceStatus.from(attendanceStatus)
        )
    }

    fun savePraise(studentId: Int, scoreDelta: Int) = viewModelScope.launch {
        try {
            RetrofitClient.studentApi.savePraise(ScoreRequest(studentId, scoreDelta))
            loadStudentsFromServer()
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    fun savePresentation(studentId: Int, scoreDelta: Int) = viewModelScope.launch {
        try {
            RetrofitClient.studentApi.savePresentation(ScoreRequest(studentId, scoreDelta))
            loadStudentsFromServer()
        } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }

    fun saveHomework(studentId: Int, homeworkName: String, homeworkScore: String, date: String) =
        viewModelScope.launch {
            try {
                RetrofitClient.studentApi.saveHomework(
                    HomeworkRequest(
                        studentId = studentId,
                        homeworkName = homeworkName,
                        homeworkScore = homeworkScore,
                        date = date
                    )
                )
                markStudentUpdated(studentId)
                loadStudentsFromServer()
            } catch (e: Exception) {
                _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
            }
        }

    fun saveHomeworkMemo(studentId: Int, homeworkName: String, homeworkMemo: String, date: String) =
        viewModelScope.launch {
            try {
                RetrofitClient.studentApi.saveHomework(
                    HomeworkRequest(
                        studentId = studentId,
                        homeworkName = homeworkName,
                        date = date,
                        homeworkMemo = homeworkMemo
                    )
                )
                markStudentUpdated(studentId)
                loadStudentsFromServer()
            } catch (e: Exception) {
                _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
            }
        }

    fun saveActivity(studentId: Int, activityName: String, activityScore: String, date: String) =
        viewModelScope.launch {
            try {
                RetrofitClient.studentApi.saveActivity(
                    ActivityRequest(
                        studentId = studentId,
                        activityName = activityName,
                        activityScore = activityScore,
                        date = date
                    )
                )
                markStudentUpdated(studentId)
                loadStudentsFromServer()
            } catch (e: Exception) {
            _serverMessage.value = "Failed: ${e.message ?: e.javaClass.simpleName}"
        }
    }
}
