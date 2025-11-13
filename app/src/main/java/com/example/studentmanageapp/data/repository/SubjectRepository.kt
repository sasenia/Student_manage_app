package com.example.studentmanageapp.data.repository

import com.example.studentmanageapp.data.dao.SubjectDao
import com.example.studentmanageapp.data.entity.Subject
import kotlinx.coroutines.flow.Flow

class SubjectRepository(private val dao: SubjectDao) {
    val subjects: Flow<List<Subject>> = dao.getAllSubjects()

    suspend fun addSubject(name: String) {
        dao.insert(Subject(name))
    }

    suspend fun deleteSubjects(names: List<String>) {
        dao.deleteByNames(names)
    }
}