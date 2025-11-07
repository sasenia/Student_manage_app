package com.example.studentmanageapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val gender: String,
    val memo: String,
    val attendance: Boolean = false,
    val homework: String = "",
    val praise: String = ""
)