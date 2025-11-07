package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun StudentNameListScreen(viewModel: StudentViewModel) {
    var newStudentId by remember { mutableStateOf("") }
    var newStudentName by remember { mutableStateOf("") }
    var studentList by remember { mutableStateOf<List<Student>>(emptyList()) }

    // DB에서 학생 목록 불러오기
    LaunchedEffect(Unit) {
        viewModel.getAllStudents { result ->
            studentList = result
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("학생 이름 관리", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = newStudentId,
            onValueChange = { newStudentId = it },
            label = { Text("학생 번호 입력") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = newStudentName,
            onValueChange = { newStudentName = it },
            label = { Text("학생 이름 입력") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val id = newStudentId.toIntOrNull()
                if (id != null && newStudentName.isNotBlank()) {
                    val newStudent = Student(
                        id = id,
                        name = newStudentName.trim(),
                        gender = "미지정",
                        memo = ""
                    )
                    viewModel.addStudent(newStudent)
                    viewModel.getAllStudents { studentList = it }
                    newStudentId = ""
                    newStudentName = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("학생 추가")
        }

        Divider()

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(studentList) { student ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("번호: ${student.id}", style = MaterialTheme.typography.bodyMedium)
                        Text("이름: ${student.name}", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}