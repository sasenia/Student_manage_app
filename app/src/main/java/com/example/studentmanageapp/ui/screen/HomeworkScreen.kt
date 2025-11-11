package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Note
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanageapp.data.entity.Subject
import com.example.studentmanageapp.viewmodel.SubjectViewModel
import androidx.compose.material3.TextField
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.studentmanageapp.viewmodel.StudentViewModel
import java.util.Calendar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkScreen(
    studentViewModel: StudentViewModel,
    subjectViewModel: SubjectViewModel = viewModel()
) {
    val studentList by studentViewModel.studentList.collectAsState()
    val subjectList by subjectViewModel.subjectList.collectAsState()

    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val selectedDateString = selectedDate.format(DateTimeFormatter.ISO_DATE)

    var showSubjectDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    val selectedSubjects = remember { mutableStateListOf<Subject>() }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = DatePickerDialog(
        context,
        { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            selectedDate = LocalDate.of(year, month + 1, dayOfMonth)
        },
        selectedDate.year,
        selectedDate.monthValue - 1,
        selectedDate.dayOfMonth
    )

    Column(modifier = Modifier.padding(16.dp)) {

        // 숙제 입력 헤더 + 날짜 + 아이콘들
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

            // 선택된 날짜 표시
            if (selectedDate != null) {
                Text(
                    selectedDate.format(dateFormatter),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // 달력 아이콘
            IconButton(onClick = { datePickerDialog.show() }) {
                Icon(Icons.Default.DateRange, contentDescription = "날짜 선택")
            }

            // 과제명 관리 아이콘
            IconButton(onClick = { showSubjectDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "과제명 관리")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 과제 선택 드롭다운
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
                            selectedSubject = subject
                            expanded = false
                        }
                    )
                }
            }
        }

        // ⬇️ 이후 평가 영역 등 계속 이어짐


        // 학생 평가
        if (selectedSubject != null) {
            Spacer(modifier = Modifier.height(11.dp)) // ✅ 1dp 줄임
            Text("학생 평가", style = MaterialTheme.typography.titleSmall.copy(fontSize = 15.sp, lineHeight = 21.sp)) // ✅ 1sp 키움
            Spacer(modifier = Modifier.height(3.dp)) // ✅ 1dp 줄임

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("번호", modifier = Modifier.weight(0.2f), style = MaterialTheme.typography.labelSmall.copy(fontSize = 15.sp, lineHeight = 21.sp))
                        Text("이름", modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.labelSmall.copy(fontSize = 15.sp, lineHeight = 21.sp))
                        Text("평가", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelSmall.copy(fontSize = 15.sp, lineHeight = 21.sp))
                    }
                }

                items(studentList.sortedBy { it.id }, key = { it.id }) { student ->
                    val subjectName = selectedSubject?.name ?: return@items
                    val dateKey = selectedDateString ?: return@items

                    val selectedLevel = student.homeworkMap[subjectName]?.get(dateKey) ?: "상"
                    val memoText = student.memoMap[subjectName]?.get(dateKey) ?: ""

                    val showMemoMap = remember { mutableStateMapOf<Int, Boolean>() }
                    val levelExpandedMap = remember { mutableStateMapOf<Int, Boolean>() }

                    val showMemo = showMemoMap[student.id] ?: false
                    val levelExpanded = levelExpandedMap[student.id] ?: false

                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(student.id.toString(), modifier = Modifier.weight(0.2f), style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, lineHeight = 21.sp))
                            Text(student.name, modifier = Modifier.weight(0.4f), style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, lineHeight = 21.sp))

                            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                                OutlinedButton(
                                    onClick = { levelExpandedMap[student.id] = true },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp), // ✅ 1dp 줄임
                                    modifier = Modifier.height(31.dp) // ✅ 1dp 줄임
                                ) {
                                    Text(selectedLevel, style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, lineHeight = 21.sp))
                                }

                                DropdownMenu(
                                    expanded = levelExpanded,
                                    onDismissRequest = { levelExpandedMap[student.id] = false }
                                ) {
                                    listOf("상", "중", "하").forEach { level ->
                                        DropdownMenuItem(
                                            text = { Text(level, style = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, lineHeight = 21.sp)) },
                                            onClick = {
                                                levelExpandedMap[student.id] = false
                                                val updatedHomeworkMap = student.homeworkMap.toMutableMap()
                                                val dateMap = updatedHomeworkMap[subjectName]?.toMutableMap() ?: mutableMapOf()
                                                dateMap[dateKey] = level
                                                updatedHomeworkMap[subjectName] = dateMap
                                                studentViewModel.updateStudent(student.copy(homeworkMap = updatedHomeworkMap))
                                            }
                                        )
                                    }
                                }
                            }

                            IconButton(onClick = {
                                showMemoMap[student.id] = !showMemo
                            }) {
                                Icon(imageVector = Icons.Default.Note, contentDescription = "메모")
                            }
                        }

                        if (showMemo) {
                            Spacer(modifier = Modifier.height(0.dp)) // ✅ 1dp 줄임
                            var newMemoText by remember { mutableStateOf(memoText) }

                            TextField(
                                value = newMemoText,
                                onValueChange = {
                                    newMemoText = it
                                    val updatedMemoMap = student.memoMap.toMutableMap()
                                    val memoDateMap = updatedMemoMap[subjectName]?.toMutableMap() ?: mutableMapOf()
                                    memoDateMap[dateKey] = it
                                    updatedMemoMap[subjectName] = memoDateMap
                                    studentViewModel.updateStudent(student.copy(memoMap = updatedMemoMap))
                                },
                                label = { Text("메모", style = MaterialTheme.typography.labelSmall.copy(fontSize = 15.sp, lineHeight = 21.sp)) },
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 15.sp, lineHeight = 21.sp),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }


        // ✅ 과제명 관리 다이얼로그
        if (showSubjectDialog) {
            Dialog(onDismissRequest = { showSubjectDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 6.dp,
                    color = Color(0xFFFFF8E1)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("과제명 관리", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = newSubjectName,
                                onValueChange = { newSubjectName = it },
                                label = { Text("과제명") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color(0xFFFFF8E1)
                                )
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

                        subjectList.forEach { subject ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedSubjects.contains(subject),
                                    onCheckedChange = { checked ->
                                        if (checked) selectedSubjects.add(subject)
                                        else selectedSubjects.remove(subject)
                                    }
                                )
                                Text(subject.name)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    subjectViewModel.deleteSubjects(selectedSubjects.map { it.name })
                                    if (selectedSubject in selectedSubjects) selectedSubject = null
                                    selectedSubjects.clear()
                                },
                                enabled = selectedSubjects.isNotEmpty()
                            ) {
                                Text("선택한 과제 삭제")
                            }

                            TextButton(onClick = { showSubjectDialog = false }) {
                                Text("닫기")
                            }
                        }
                    }
                }
            }
        }
    }
}