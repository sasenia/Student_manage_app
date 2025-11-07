package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun ActivityScreen(viewModel: StudentViewModel) {
    var studentList by remember { mutableStateOf<List<Student>>(emptyList()) }

    // DB에서 학생 목록 불러오기
    LaunchedEffect(Unit) {
        viewModel.getAllStudents { result ->
            studentList = result
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("참여 확인", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        studentList.forEach { student ->
            Text(student.name)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}