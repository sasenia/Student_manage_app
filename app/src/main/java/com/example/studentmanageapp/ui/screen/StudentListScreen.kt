package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(viewModel: StudentViewModel) {
    val studentList by viewModel.studentList.collectAsState()
    var editingMemo by remember { mutableStateOf("") }
    var editingStudent by remember { mutableStateOf<Student?>(null) }
    var showMemoDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Text("학생 명단", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("번호", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            Text("이름", modifier = Modifier.weight(2f), style = MaterialTheme.typography.labelMedium)
            Text("성별", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
            Text("메모", modifier = Modifier.weight(3f), style = MaterialTheme.typography.labelMedium)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(studentList.sortedBy { it.id }) { student ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("${student.id}", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Text(student.name, modifier = Modifier.weight(2f), style = MaterialTheme.typography.bodyMedium)
                    Text(student.gender, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Box(modifier = Modifier.weight(3f)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = student.memo,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    editingMemo = student.memo
                                    editingStudent = student
                                    showMemoDialog = true
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "메모 수정")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showMemoDialog && editingStudent != null) {
        AlertDialog(
            onDismissRequest = { showMemoDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    val updated = editingStudent!!.copy(memo = editingMemo)
                    viewModel.updateStudent(updated)
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
                    label = { Text("메모") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFFFF8E1)
                    )
                )
            }
        )
    }
}