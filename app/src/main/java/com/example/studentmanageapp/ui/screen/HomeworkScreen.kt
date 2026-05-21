package com.example.studentmanageapp.ui.screen

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.studentmanageapp.data.entity.Subject
import com.example.studentmanageapp.viewmdel.HomeworkUiViewModel
import com.example.studentmanageapp.viewmodel.StudentViewModel
import com.example.studentmanageapp.viewmodel.SubjectViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkScreen(
    studentViewModel: StudentViewModel,
    subjectViewModel: SubjectViewModel,
    homeworkUiViewModel: HomeworkUiViewModel
) {
    val studentList by studentViewModel.studentList.collectAsState()
    val subjectList by subjectViewModel.subjectList.collectAsState()

    val uiState = homeworkUiViewModel.uiState
    val selectedDate = uiState.selectedDate
    val selectedSubject =
        subjectList.find { it.name == uiState.selectedSubjectName }

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val selectedDateString = selectedDate.format(DateTimeFormatter.ISO_DATE)

    var showSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    val selectedSubjects = remember { mutableStateListOf<Subject>() }

    val context = LocalContext.current

    var showMemoDialog by remember { mutableStateOf(false) }
    var memoText by remember { mutableStateOf("") }
    var selectedStudentForMemo by remember { mutableStateOf<Int?>(null) }


    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            homeworkUiViewModel.selectDate(
                LocalDate.of(year, month + 1, day)
            )
        },
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    )

    Column(modifier = Modifier.padding(16.dp)) {

        /* ================= 헤더 ================= */
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "과제 검사",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            Text(selectedDate.format(dateFormatter))

            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = "날짜 선택")
            }

            IconButton(onClick = { showSubjectDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "과제명 관리")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        /* ================= 과제 선택 ================= */
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedSubject?.name ?: "과제 선택")
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                subjectList.forEach { subject ->
                    DropdownMenuItem(
                        text = { Text(subject.name) },
                        onClick = {
                            homeworkUiViewModel.selectSubject(subject.name)
                            expanded = false
                        }
                    )
                }
            }
        }

        /* ================= 학생 평가 ================= */
        if (selectedSubject != null) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {

                item {
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("번호", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("이름", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("평가", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("메모", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }

                items(studentList.sortedBy { it.id }, key = { it.id }) { student ->

                    val isLastUpdated =
                        student.id == studentViewModel.lastUpdatedStudentId

                    val subjectName = selectedSubject.name
                    val dateKey = selectedDateString

                    val selectedLevel =
                        student.homeworkMap[subjectName]?.get(dateKey)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isLastUpdated)
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                else
                                    Color.Transparent
                            )
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            student.id.toString(),
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Text(
                            student.name,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.Center
                        )

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            var expandedLevel by remember { mutableStateOf(false) }

                            OutlinedButton(
                                onClick = { expandedLevel = true },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(selectedLevel ?: " ")
                            }

                            DropdownMenu(
                                expanded = expandedLevel,
                                onDismissRequest = { expandedLevel = false },
                                modifier = Modifier.width(56.dp)
                            ) {
                                listOf("상", "중", "하").forEach { level ->
                                    DropdownMenuItem(
                                        text = {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(28.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = level,
                                                    textAlign = TextAlign.Center,
                                                    style = MaterialTheme.typography.bodySmall
                                                )
                                            }
                                        },
                                        onClick = {
                                            expandedLevel = false
                                            val updated = student.homeworkMap.toMutableMap()
                                            val dateMap =
                                                updated[subjectName]?.toMutableMap()
                                                    ?: mutableMapOf()
                                            dateMap[dateKey] = level
                                            updated[subjectName] = dateMap

                                            studentViewModel.updateStudent(
                                                student.copy(homeworkMap = updated)
                                            )
                                            // ✅ 핵심
                                            studentViewModel.markStudentUpdated(student.id)
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    )
                                }
                            }
                        }

                        IconButton(
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedStudentForMemo = student.id
                                memoText = student.memo ?: ""
                                showMemoDialog = true
                            }
                        ) {
                            Icon(Icons.Default.Note, contentDescription = "메모")
                        }
                    }
                }

            }
        }
    }

    /* ================= 과제명 관리 다이얼로그 ================= */
    if (showSubjectDialog) {
        Dialog(onDismissRequest = { showSubjectDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color(0xFFFFF8E1)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("과제명 관리", style = MaterialTheme.typography.titleLarge)

                    Spacer(modifier = Modifier.height(12.dp))

                    Row {
                        TextField(
                            value = newSubjectName,
                            onValueChange = { newSubjectName = it },
                            modifier = Modifier.weight(1f),
                            label = { Text("과제명") }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (newSubjectName.isNotBlank()) {
                                subjectViewModel.addSubject(newSubjectName)
                                newSubjectName = ""
                            }
                        }) {
                            Text("추가")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = { showSubjectDialog = false }) {
                        Text("닫기")
                    }
                }
            }
        }
    }
    if (showMemoDialog && selectedStudentForMemo != null) {
        Dialog(onDismissRequest = { showMemoDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Text(
                        text = "메모",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextField(
                        value = memoText,
                        onValueChange = { memoText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("메모를 입력하세요") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = { showMemoDialog = false }) {
                            Text("취소")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            val student = studentList.first { it.id == selectedStudentForMemo }

                            studentViewModel.updateStudent(
                                student.copy(memo = memoText)
                            )

                            showMemoDialog = false
                        }) {
                            Text("저장")
                        }
                    }
                }
            }
        }
    }
}
