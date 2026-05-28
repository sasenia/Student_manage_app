package com.example.studentmanageapp.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface StudentApi {

    @GET("students")
    suspend fun getStudents(): List<StudentResponse>

    @POST("students")
    suspend fun addStudent(@Body student: StudentRequest): StudentResponse

    @PUT("students/{id}")
    suspend fun updateStudent(
        @Path("id") id: Int,
        @Body student: StudentRequest
    ): StudentResponse

    @DELETE("students/{id}")
    suspend fun deleteStudent(@Path("id") id: Int)

    @GET("praise")
    suspend fun getPraises(): List<ScoreResponse>

    @POST("praise")
    suspend fun savePraise(@Body request: ScoreRequest)

    @GET("presentation")
    suspend fun getPresentations(): List<ScoreResponse>

    @POST("presentation")
    suspend fun savePresentation(@Body request: ScoreRequest)

    @GET("homework")
    suspend fun getHomeworks(): List<HomeworkResponse>

    @POST("homework")
    suspend fun saveHomework(@Body request: HomeworkRequest)

    @GET("activity")
    suspend fun getActivities(): List<ActivityResponse>

    @POST("activity")
    suspend fun saveActivity(@Body request: ActivityRequest)

    @GET("attendance")
    suspend fun getAttendances(
        @Query("month") month: String,
        @Query("status") status: String? = null
    ): List<AttendanceResponse>

    @POST("attendance")
    suspend fun saveAttendance(@Body request: AttendanceRequest): AttendanceResponse

    @DELETE("attendance")
    suspend fun deleteAttendanceByDate(@Query("date") date: String)
}
