package com.example.studentmanageapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.studentmanageapp.StudentManageApp
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.ui.screen.*
import com.example.studentmanageapp.viewmodel.StudentViewModel
import com.example.studentmanageapp.viewmodel.SubjectViewModel
import com.example.studentmanageapp.viewmodel.SubjectViewModelFactory
import java.time.LocalDate
import androidx.compose.ui.platform.LocalContext
import com.example.studentmanageapp.data.entity.Student

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: StudentViewModel
) {
    val attendanceRecords = remember { mutableStateListOf<AttendanceRecord>() }

    val context = LocalContext.current
    val subjectViewModel: SubjectViewModel = viewModel(
        factory = SubjectViewModelFactory(
            (context.applicationContext as StudentManageApp).subjectRepository
        )
    )

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
        composable(
            route = "${Screen.Attendance.route}/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val dateString = backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            val selectedDate = LocalDate.parse(dateString)

            AttendanceScreen(
                selectedDate = selectedDate,
                viewModel = viewModel,
                attendanceRecords = attendanceRecords,
                navController = navController
            )
        }
        composable(Screen.Homework.route) {
            HomeworkScreen(
                studentViewModel = viewModel,
                subjectViewModel = subjectViewModel
            )
        }
        composable(Screen.Activity.route) {
            ActivityScreen(viewModel)
        }
        composable(Screen.Praise.route) {
            PraiseScreen(viewModel)
        }
        composable(Screen.Options.route) {
            OptionScreen(navController)
        }
        composable(Screen.Check.route) {
            CheckScreen(viewModel = viewModel, navController = navController)
        }
        composable("optionScreen") {
            OptionScreen(navController)
        }

        composable("studentAddScreen") {
            val student = Student(
                id = 0,
                name = "",
                gender = "남",
                memo = "",
                praiseScore = 0,
                activityMap = emptyMap()
            )
            StudentEditScreen(
                student = student,
                viewModel = viewModel,
                navController = navController
            )
        }

        composable(
            route = "studentEditScreen?studentId={studentId}",
            arguments = listOf(
                navArgument("studentId") {
                    type = NavType.IntType
                    defaultValue = -1
                }
            )
        ) { backStackEntry ->
            val studentId = backStackEntry.arguments?.getInt("studentId") ?: -1
            val studentList = viewModel.studentList.collectAsState().value
            val student = studentList.find { it.id == studentId } ?: Student(
                id = 0,
                name = "",
                gender = "남",
                memo = "",
                praiseScore = 0,
                activityMap = emptyMap()
            )

            StudentEditScreen(
                student = student,
                viewModel = viewModel,
                navController = navController
            )
        }

    }
}