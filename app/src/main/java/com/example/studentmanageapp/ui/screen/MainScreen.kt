package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studentmanageapp.ui.navigation.Screen
import com.example.studentmanageapp.viewmodel.StudentViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun MainScreen(
    navController: NavHostController,
    viewModel: StudentViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("기능 선택", style = MaterialTheme.typography.titleLarge)

        FeatureButton("학생 명단") {
            navController.navigate(Screen.StudentList.route)
        }

        FeatureButton("출석") {
            val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
            navController.navigate(Screen.Attendance.routeWithDate(today))
        }

        FeatureButton("칭찬") {
            navController.navigate(Screen.Praise.route)
        }

        FeatureButton("발표") {
            navController.navigate(Screen.Presentation.route)
        }

        FeatureButton("과제") {
            navController.navigate(Screen.Homework.route)
        }

        FeatureButton("확인") {
            navController.navigate(Screen.Activity.route)
        }
    }
}

@Composable
fun FeatureButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.92f)
            .height(70.dp)
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp
        )
    }
}
