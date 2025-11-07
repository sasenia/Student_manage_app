package com.example.studentmanageapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.studentmanageapp.data.database.AppDatabase
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.data.repository.StudentRepository
import kotlinx.coroutines.launch

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "student_database"
    ).build()

    private val repository = StudentRepository(db.studentDao())

    fun addStudent(student: Student) = viewModelScope.launch {
        repository.addStudent(student)
    }

    fun updateStudent(student: Student) = viewModelScope.launch {
        repository.updateStudent(student)
    }

    fun deleteStudent(student: Student) = viewModelScope.launch {
        repository.deleteStudent(student)
    }

    fun getAllStudents(onResult: (List<Student>) -> Unit) = viewModelScope.launch {
        val students = repository.getAllStudents()
        onResult(students)
    }
}