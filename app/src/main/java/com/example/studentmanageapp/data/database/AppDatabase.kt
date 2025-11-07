package com.example.studentmanageapp.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studentmanageapp.data.dao.StudentDao
import com.example.studentmanageapp.data.entity.Student

@Database(entities = [Student::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}