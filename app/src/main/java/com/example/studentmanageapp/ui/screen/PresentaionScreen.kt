package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun PresentationScreen(
    viewModel: StudentViewModel
) {
    val studentList by viewModel.studentList.collectAsState()
    val scoreMap = remember { mutableStateMapOf<Int, Int>() }

    var showDialog by remember { mutableStateOf(false) }
    var dialogAction by remember { mutableStateOf("plus") }
    val selectedStudents = remember { mutableStateListOf<Int>() }

    LaunchedEffect(studentList) {
        studentList.forEach { student ->
            scoreMap[student.id] = student.presentationScore
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ---------- 상단 제목 + 일괄 버튼 ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("발표", style = MaterialTheme.typography.titleLarge)

            Row {
                IconButton(onClick = {
                    dialogAction = "minus"
                    selectedStudents.clear()
                    showDialog = true
                }) {
                    Icon(Icons.Default.Remove, contentDescription = "일괄 감소")
                }

                IconButton(onClick = {
                    dialogAction = "plus"
                    selectedStudents.clear()
                    showDialog = true
                }) {
                    Icon(Icons.Default.Add, contentDescription = "일괄 증가")
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* ---------- 헤더 ---------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("번호", modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
            Text("이름", modifier = Modifier.weight(1.4f), textAlign = TextAlign.Center)
            Text("점수", modifier = Modifier.weight(1.8f), textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(8.dp))

        /* ---------- 학생 리스트 (개별 증감) ---------- */
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(studentList.sortedBy { it.id }) { student ->
                val score = scoreMap[student.id] ?: 0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // 번호
                    Text(
                        student.id.toString(),
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )

                    // 이름
                    Text(
                        student.name,
                        modifier = Modifier.weight(1.4f),
                        textAlign = TextAlign.Center,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // ⭐ 점수 영역 (- 0 +)
                    Row(
                        modifier = Modifier.weight(1.8f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {

                        // 감소
                        IconButton(
                            onClick = {

                                val newScore = score - 1
                                scoreMap[student.id] = newScore
                                viewModel.savePresentation(student.id, -1)

                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "감소")
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // 점수
                        Text(
                            text = score.toString(),
                            modifier = Modifier.width(40.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // 증가
                        IconButton(
                            onClick = {
                                val newScore = score + 1
                                scoreMap[student.id] = newScore
                                viewModel.savePresentation(student.id, 1)
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "증가")
                        }
                    }
                }
            }
        }
    }

    /* ---------- 일괄 증감 다이얼로그 ---------- */
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = {
                Text(
                    if (dialogAction == "plus")
                        "학생 선택 후 +1 적용"
                    else
                        "학생 선택 후 -1 적용"
                )
            },
            text = {
                LazyColumn {
                    items(studentList.sortedBy { it.id }) { student ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
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
            },
            confirmButton = {
                TextButton(onClick = {
                    val delta = if (dialogAction == "plus") 1 else -1
                    selectedStudents.forEach { id ->
                        val current = scoreMap[id] ?: 0
                        val newScore = current + delta
                        scoreMap[id] = newScore
                        viewModel.savePresentation(id, delta)
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
            }
        )
    }
}
