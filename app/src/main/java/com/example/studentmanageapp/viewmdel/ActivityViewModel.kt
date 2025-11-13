package com.example.studentmanageapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentmanageapp.data.repository.ActivityRepository
import com.example.studentmanageapp.data.entity.Activity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ActivityViewModel(private val repository: ActivityRepository) : ViewModel() {

    val activityList: StateFlow<List<Activity>> =
        repository.activityList.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addActivity(name: String) {
        viewModelScope.launch {
            repository.addActivity(name)
        }
    }

    fun deleteActivities(names: List<String>) {
        viewModelScope.launch {
            repository.deleteActivities(names)
        }
    }
}