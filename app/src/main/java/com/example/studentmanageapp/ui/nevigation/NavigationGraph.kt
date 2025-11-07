package com.example.studentmanageapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateListOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.ui.screen.*
import com.example.studentmanageapp.viewmodel.StudentViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: StudentViewModel
) {
    // UI 상태용 출석 기록 리스트
    val attendanceRecords = remember { mutableStateListOf<AttendanceRecord>() }

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route
    ) {
        composable(Screen.Main.route) {
            MainScreen(navController)
        }
        composable(Screen.StudentList.route) {
            StudentListScreen(viewModel)
        }
        composable(Screen.Attendance.route) {
            AttendanceScreen(viewModel, attendanceRecords)
        }
        composable(Screen.Homework.route) {
            HomeworkScreen(viewModel)
        }
        composable(Screen.Activity.route) {
            ActivityScreen(viewModel)
        }
        composable(Screen.Praise.route) {
            PraiseScreen(viewModel)
        }
        composable(Screen.Options.route) {
            OptionsScreen(viewModel)
        }
    }
}