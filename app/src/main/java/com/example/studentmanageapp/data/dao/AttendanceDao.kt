package com.example.studentmanageapp.data.dao

import androidx.room.*
import com.example.studentmanageapp.model.AttendanceRecord
import java.time.LocalDate

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AttendanceRecord)

    @Query("SELECT * FROM attendance_records WHERE date BETWEEN :start AND :end")
    suspend fun getRecordsBetween(start: LocalDate, end: LocalDate): List<AttendanceRecord>

    @Query("SELECT * FROM attendance_records WHERE strftime('%Y-%m', date) = :yearMonth")
    suspend fun getRecordsByMonth(yearMonth: String): List<AttendanceRecord>

    // ✅ 특정 날짜의 출석 기록 불러오기
    @Query("SELECT * FROM attendance_records WHERE date = :date")
    suspend fun getRecordsByDate(date: LocalDate): List<AttendanceRecord>

    // ✅ 전체 출석 기록 불러오기
    @Query("SELECT * FROM attendance_records")
    suspend fun getAllRecords(): List<AttendanceRecord>

    // ✅ 특정 학생의 출석 기록 삭제
    @Query("DELETE FROM attendance_records WHERE studentId = :studentId")
    suspend fun deleteByStudentId(studentId: Int)

    @Query("DELETE FROM attendance_records WHERE studentId = :studentId AND date = :date")
    suspend fun deleteRecord(studentId: Int, date: LocalDate)
}