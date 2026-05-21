package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* ---------- 제목 ---------- */
        Text(
            "학생 명단",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        /* ---------- 헤더 ---------- */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("번호", modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
            Text("이름", modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center)
            Text("성별", modifier = Modifier.weight(0.8f), textAlign = TextAlign.Center)
            Text("메모", modifier = Modifier.weight(2.2f), textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(8.dp))

        /* ---------- 학생 리스트 ---------- */
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(studentList.sortedBy { it.id }) { student ->

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        student.id.toString(),
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        student.name,
                        modifier = Modifier.weight(1.2f),
                        textAlign = TextAlign.Center
                    )

                    Text(
                        student.gender,
                        modifier = Modifier.weight(0.8f),
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.weight(2.2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 메모 미리보기 (1줄 고정)
                        Text(
                            text = student.memo,
                            modifier = Modifier.weight(1f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        IconButton(
                            onClick = {
                                editingMemo = student.memo
                                editingStudent = student
                                showMemoDialog = true
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "메모 수정")
                        }
                    }
                }
            }
        }
    }

    /* ---------- 메모 수정 다이얼로그 ---------- */
    if (showMemoDialog && editingStudent != null) {
        AlertDialog(
            onDismissRequest = { showMemoDialog = false },
            title = { Text("메모 수정") },
            text = {
                TextField(
                    value = editingMemo,
                    onValueChange = { editingMemo = it },
                    placeholder = { Text("메모를 입력하세요") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFFFF8E1)
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val updated = editingStudent!!.copy(memo = editingMemo)
                    viewModel.updateStudent(updated)
                    viewModel.markStudentUpdated(updated.id)
                    showMemoDialog = false
                }) {
                    Text("저장")
                }
            },
            dismissButton = {
                TextButton(onClick = { showMemoDialog = false }) {
                    Text("취소")
                }
            }
        )
    }
}
