package com.example.studentmanageapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studentmanageapp.data.dao.StudentDao
import com.example.studentmanageapp.data.dao.AttendanceDao
import com.example.studentmanageapp.data.dao.SubjectDao
import com.example.studentmanageapp.data.dao.ActivityDao
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.data.entity.Subject
import com.example.studentmanageapp.data.entity.Activity
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.model.AttendanceStatusConverter
import com.example.studentmanageapp.data.converter.LocalDateConverter

@Database(
    entities = [Student::class, AttendanceRecord::class, Subject::class, Activity::class], // ✅ Activity 추가
    version = 9, // ✅ 버전 증가
    exportSchema = false
)
@TypeConverters(AttendanceStatusConverter::class, LocalDateConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao
    abstract fun subjectDao(): SubjectDao
    abstract fun activityDao(): ActivityDao // ✅ Activity DAO 추가

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "student_database"
                )
                    .fallbackToDestructiveMigration() // ✅ 버전 변경 시 데이터 초기화
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}