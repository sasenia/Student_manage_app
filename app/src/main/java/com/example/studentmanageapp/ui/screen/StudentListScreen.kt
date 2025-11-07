package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun StudentListScreen(viewModel: StudentViewModel) {
    var studentList by remember { mutableStateOf<List<Student>>(emptyList()) }
    var editingMemo by remember { mutableStateOf("") }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var showMemoDialog by remember { mutableStateOf(false) }

    // DB에서 학생 목록 불러오기
    LaunchedEffect(Unit) {
        viewModel.getAllStudents { result ->
            studentList = result
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("학생 명단", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        studentList.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("${student.id}  ${student.name}  ${student.gender}  ${student.memo}")
                IconButton(onClick = {
                    editingMemo = student.memo
                    editingStudent = student
                    showMemoDialog = true
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "메모 수정")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }

    if (showMemoDialog && editingStudent != null) {
        AlertDialog(
            onDismissRequest = { showMemoDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val updated = editingStudent!!.copy(memo = editingMemo)
                    viewModel.updateStudent(updated)
                    viewModel.getAllStudents { studentList = it }
                    showMemoDialog = false
                }) {
                    Text("저장")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMemoDialog = false }) {
                    Text("취소")
                }
            },
            title = { Text("메모 수정") },
            text = {
                TextField(
                    value = editingMemo,
                    onValueChange = { editingMemo = it },
                    label = { Text("메모") }
                )
            }
        )
    }
}