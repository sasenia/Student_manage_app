package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun HomeworkScreen(viewModel: StudentViewModel) {
    var studentList by remember { mutableStateOf<List<Student>>(emptyList()) }
    val homeworkMap = remember { mutableStateMapOf<Int, String>() }

    // DB에서 학생 목록 불러오기
    LaunchedEffect(Unit) {
        viewModel.getAllStudents { result ->
            studentList = result
            result.forEach { student ->
                homeworkMap[student.id] = student.homework
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("과제 입력", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        studentList.forEach { student ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(text = student.name)
                Spacer(modifier = Modifier.height(4.dp))
                TextField(
                    value = homeworkMap[student.id] ?: "",
                    onValueChange = { newText ->
                        homeworkMap[student.id] = newText
                        val updated = student.copy(homework = newText)
                        viewModel.updateStudent(updated)
                    },
                    label = { Text("과제 내용") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}