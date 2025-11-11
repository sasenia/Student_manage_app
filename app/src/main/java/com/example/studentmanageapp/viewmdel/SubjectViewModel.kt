package com.example.studentmanageapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studentmanageapp.data.entity.Subject
import com.example.studentmanageapp.data.repository.SubjectRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SubjectViewModel(private val repository: SubjectRepository) : ViewModel() {

    val subjectList: StateFlow<List<Subject>> =
        repository.subjects.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    fun addSubject(name: String) {
        viewModelScope.launch {
            repository.addSubject(name)
        }
    }

    fun deleteSubjects(names: List<String>) {
        viewModelScope.launch {
            repository.deleteSubjects(names)
        }
    }
}