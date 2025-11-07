package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studentmanageapp.ui.navigation.Screen

@Composable
fun MainScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("기능 선택", style = MaterialTheme.typography.titleLarge)

        FeatureButton("학생 명단") { navController.navigate(Screen.StudentList.route) }
        FeatureButton("출석 체크") { navController.navigate(Screen.Attendance.route) }
        FeatureButton("과제 입력") { navController.navigate(Screen.Homework.route) }
        FeatureButton("참여 확인") { navController.navigate(Screen.Activity.route) }
        FeatureButton("칭찬/발표") { navController.navigate(Screen.Praise.route) }
    }
}

@Composable
fun FeatureButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(0.92f) // 양쪽 여백 살짝 남기고 거의 꽉 채움
            .height(70.dp) // 세로 길이 살짝 늘림
            .padding(horizontal = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp // 텍스트도 살짝 키움
        )
    }
}