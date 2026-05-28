package com.example.studentmanageapp.network

data class ScoreRequest(
    val studentId: Int,
    val score: Int
)

data class ScoreResponse(
    val studentId: Int,
    val name: String,
    val score: Int
)

data class HomeworkRequest(
    val studentId: Int,
    val homeworkName: String,
    val homeworkScore: String? = null,
    val date: String,
    val homeworkMemo: String? = null
)

data class HomeworkResponse(
    val studentId: Int,
    val name: String,
    val homeworkName: String,
    val homeworkScore: String? = null,
    val date: String,
    val homeworkMemo: String? = null
)

data class ActivityRequest(
    val studentId: Int,
    val activityName: String,
    val activityScore: String,
    val date: String
)

data class ActivityResponse(
    val studentId: Int,
    val name: String,
    val activityName: String,
    val activityScore: String,
    val date: String
)

data class AttendanceRequest(
    val studentId: Int,
    val attendanceStatus: String,
    val date: String
)

data class AttendanceResponse(
    val studentId: Int,
    val name: String,
    val date: String,
    val attendanceStatus: String
)
