package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun PraiseScreen(viewModel: StudentViewModel) {
    val studentList by viewModel.studentList.collectAsState()
    val scoreMap = remember { mutableStateMapOf<Int, Int>() }

    var showDialog by remember { mutableStateOf(false) }
    var dialogAction by remember { mutableStateOf("plus") }
    val selectedStudents = remember { mutableStateListOf<Int>() }

    // 초기 점수 세팅
    LaunchedEffect(studentList) {
        studentList.forEach { student ->
            if (!scoreMap.containsKey(student.id)) {
                scoreMap[student.id] = student.praiseScore
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "칭찬/발표",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            IconButton(onClick = {
                dialogAction = "minus"
                selectedStudents.clear()
                showDialog = true
            }) {
                Icon(Icons.Default.Remove, contentDescription = "다중 점수 감소")
            }

            IconButton(onClick = {
                dialogAction = "plus"
                selectedStudents.clear()
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "다중 점수 증가")
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(studentList.sortedBy { it.id }) { student ->
                    val score = scoreMap[student.id] ?: 0
                    val scoreColor = if (score >= 0) Color.Blue else Color.Red

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp)
                    ) {
                        Text(
                            "${student.id}",
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                            textAlign = TextAlign.Start
                        )
                        Text(
                            student.name,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 4.dp),
                            style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                            textAlign = TextAlign.Start
                        )
                    }
                }
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    confirmButton = {
                        TextButton(onClick = {
                            val delta = if (dialogAction == "plus") 1 else -1
                            selectedStudents.forEach { studentId ->
                                val current = scoreMap[studentId] ?: 0
                                val newScore = current + delta
                                scoreMap[studentId] = newScore
                                val student = studentList.find { it.id == studentId }
                                if (student != null) {
                                    viewModel.updateStudent(student.copy(praiseScore = newScore))
                                }
                            }
                            showDialog = false
                        }) {
                            Text("확인")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("취소")
                        }
                    },
                    title = {
                        Text(if (dialogAction == "plus") "학생 선택 후 +1 적용" else "학생 선택 후 -1 적용")
                    },
                    text = {
                        val scrollState = rememberScrollState()
                        Column(modifier = Modifier.verticalScroll(scrollState)) {
                            studentList.sortedBy { it.id }.forEach { student ->
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Checkbox(
                                        checked = selectedStudents.contains(student.id),
                                        onCheckedChange = { checked ->
                                            if (checked) selectedStudents.add(student.id)
                                            else selectedStudents.remove(student.id)
                                        }
                                    )
                                    Text("${student.id}. ${student.name}")
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}