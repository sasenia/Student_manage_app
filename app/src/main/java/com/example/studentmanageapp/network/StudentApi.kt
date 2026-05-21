package com.example.studentmanageapp.network

import retrofit2.http.GET

interface StudentApi {

    @GET("students")
    suspend fun getStudents(): List<String>
}