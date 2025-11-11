package com.example.studentmanageapp.data.repository

import com.example.studentmanageapp.data.dao.ActivityDao
import com.example.studentmanageapp.data.entity.Activity
import kotlinx.coroutines.flow.Flow

class ActivityRepository(private val dao: ActivityDao) {
    val activityList: Flow<List<Activity>> = dao.getAllActivities()

    suspend fun addActivity(name: String) {
        dao.insert(Activity(name))
    }

    suspend fun deleteActivities(names: List<String>) {
        dao.deleteByNames(names)
    }
}