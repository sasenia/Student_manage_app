package com.example.studentmanageapp.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    object Main : Screen("main", "메인", Icons.Filled.Home)
    object StudentList : Screen("student_list", "명단", Icons.Filled.Person)
    object Attendance : Screen("attendance", "출석", Icons.Filled.CheckCircle)
    object AttendanceHistory : Screen("attendance_history", "출석 기록", Icons.Filled.List)
    object Homework : Screen("homework", "숙제", Icons.Filled.Edit)
    object Activity : Screen("activity", "활동", Icons.Filled.Star)
    object Praise : Screen("praise", "칭찬", Icons.Filled.Favorite)
    object Options : Screen("options", "설정", Icons.Filled.Settings)
}