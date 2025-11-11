package com.example.studentmanageapp.data.repository

import com.example.studentmanageapp.data.dao.StudentDao
import com.example.studentmanageapp.data.dao.AttendanceDao
import com.example.studentmanageapp.data.entity.Student

class StudentRepository(
    private val studentDao: StudentDao,
    private val attendanceDao: AttendanceDao // ✅ 출석 DAO 추가
) {

    suspend fun addStudent(student: Student) = studentDao.insert(student)

    suspend fun updateStudent(student: Student) = studentDao.update(student)

    suspend fun deleteStudent(student: Student) = studentDao.delete(student)

    suspend fun getAllStudents(): List<Student> = studentDao.getAll()

    suspend fun deleteStudentById(id: Int) = studentDao.deleteById(id)

    // ✅ 학생과 출석 함께 삭제하는 함수 추가
    suspend fun deleteStudentWithAttendance(student: Student) {
        attendanceDao.deleteByStudentId(student.id) // 출석 먼저 삭제
        studentDao.delete(student) // 학생 삭제
    }
}