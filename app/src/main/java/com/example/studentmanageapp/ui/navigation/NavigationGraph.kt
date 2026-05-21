package com.example.studentmanageapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.studentmanageapp.StudentManageApp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.ui.screen.ActivityScreen
import com.example.studentmanageapp.ui.screen.AttendanceScreen
import com.example.studentmanageapp.ui.screen.CheckScreen
import com.example.studentmanageapp.ui.screen.HomeworkScreen
import com.example.studentmanageapp.ui.screen.MainScreen
import com.example.studentmanageapp.ui.screen.OptionScreen
import com.example.studentmanageapp.ui.screen.PraiseScreen
import com.example.studentmanageapp.ui.screen.PresentationScreen
import com.example.studentmanageapp.ui.screen.StudentEditScreen
import com.example.studentmanageapp.ui.screen.StudentListScreen
import com.example.studentmanageapp.viewmdel.ActivityUiViewModel
import com.example.studentmanageapp.viewmdel.HomeworkUiViewModel
import com.example.studentmanageapp.viewmodel.StudentViewModel
import com.example.studentmanageapp.viewmodel.SubjectViewModel
import com.example.studentmanageapp.viewmodel.SubjectViewModelFactory
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavigationGraph(
    navController: NavHostController,
    viewModel: StudentViewModel,
    paddingValues: PaddingValues
) {
    val attendanceRecords = remember { mutableStateListOf<AttendanceRecord>() }

    val context = LocalContext.current
    val subjectViewModel: SubjectViewModel = viewModel(
        factory = SubjectViewModelFactory(
            (context.applicationContext as StudentManageApp).subjectRepository
        )
    )

    // ✅ 여기서 한번만 만들고, 아래 화면들에 "주입"하면 앱 종료 전까지 유지됨
    val activityUiViewModel: ActivityUiViewModel = viewModel()
    val homeworkUiViewModel: HomeworkUiViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Main.route,
        modifier = Modifier.padding(paddingValues)
    ) {
        composable(Screen.Main.route) {
            MainScreen(navController, viewModel)
        }

        composable(Screen.StudentList.route) {
            StudentListScreen(viewModel)
        }

        composable(
            route = "${Screen.Attendance.route}/{date}",
            arguments = listOf(navArgument("date") { type = NavType.StringType })
        ) { backStackEntry ->
            val dateString =
                backStackEntry.arguments?.getString("date") ?: LocalDate.now().toString()
            val selectedDate = LocalDate.parse(dateString)

            AttendanceScreen(
                selectedDate = selectedDate,
                viewModel = viewModel,
                attendanceRecords = attendanceRecords,
                navController = navController
            )
        }
        composable(Screen.Presentation.route) {
            PresentationScreen(viewModel)
        }


        // ✅ Homework: 새로 만들지 말고 위에서 만든 VM을 그대로 넘김
        composable(Screen.Homework.route) {
            HomeworkScreen(
                studentViewModel = viewModel,
                subjectViewModel = subjectViewModel,
                homeworkUiViewModel = homeworkUiViewModel
            )
        }

        // ✅ Activity: 새로 만들지 말고 위에서 만든 VM을 그대로 넘김
        composable(Screen.Activity.route) {
            ActivityScreen(
                studentViewModel = viewModel,
                activityUiViewModel = activityUiViewModel
            )
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
