package com.example.studentmanageapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    object Main : Screen("mainScreen", "홈", Icons.Default.Home)
    object StudentList : Screen("studentListScreen", "학생명단", Icons.Default.Person)
    object Attendance : Screen("attendanceScreen", "출석", Icons.Default.CheckCircle) {
        // ✅ 날짜를 포함한 경로 생성 함수
        fun routeWithDate(date: String): String = "$route/$date"
    }

    object Homework : Screen("homeworkScreen", "과제", Icons.Default.Edit)
    object Activity : Screen("activityScreen", "확인", Icons.Default.Star)
    object Praise : Screen("praiseScreen", "칭찬발표", Icons.Default.Favorite)
    object Options : Screen("optionsScreen", "설정", Icons.Default.Settings)
    object Check : Screen("checkScreen", "출석확인", Icons.Default.DateRange)
    object Calendar : Screen("calendarScreen", "달력", Icons.Default.DateRange)

    // ✅ 추가된 화면들
    object StudentAdd : Screen("studentAddScreen", "학생 추가", Icons.Default.PersonAdd)
}