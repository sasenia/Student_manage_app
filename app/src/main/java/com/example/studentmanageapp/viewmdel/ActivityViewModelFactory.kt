package com.example.studentmanageapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.studentmanageapp.data.database.AppDatabase
import com.example.studentmanageapp.data.repository.ActivityRepository

class ActivityViewModelFactory(
    private val repository: ActivityRepository
) : ViewModelProvider.Factory {

    // ✅ Context 기반 생성자 추가
    constructor(context: Context) : this(
        ActivityRepository(AppDatabase.getInstance(context).activityDao())
    )

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ActivityViewModel::class.java)) {
            return ActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}