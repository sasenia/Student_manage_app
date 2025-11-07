package com.example.studentmanageapp.data.repository

import com.example.studentmanageapp.data.dao.StudentDao
import com.example.studentmanageapp.data.entity.Student

class StudentRepository(private val dao: StudentDao) {

    suspend fun addStudent(student: Student) = dao.insert(student)

    suspend fun updateStudent(student: Student) = dao.update(student)

    suspend fun deleteStudent(student: Student) = dao.delete(student)

    suspend fun getAllStudents(): List<Student> = dao.getAll()

    suspend fun deleteStudentById(id: Int) = dao.deleteById(id)
}