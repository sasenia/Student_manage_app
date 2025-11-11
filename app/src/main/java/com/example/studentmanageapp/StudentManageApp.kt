package com.example.studentmanageapp

import android.app.Application
import com.example.studentmanageapp.data.database.AppDatabase
import com.example.studentmanageapp.data.repository.SubjectRepository

class StudentManageApp : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var subjectRepository: SubjectRepository
        private set

    override fun onCreate() {
        super.onCreate()

        // Room DB 초기화
        database = AppDatabase.getInstance(applicationContext)

        // Repository 초기화
        subjectRepository = SubjectRepository(database.subjectDao())
    }
}