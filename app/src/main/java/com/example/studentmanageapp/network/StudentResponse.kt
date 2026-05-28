package com.example.studentmanageapp.network

data class StudentResponse(
    val id: Int,
    val name: String,
    val gender: String,
    val memo: String?
)