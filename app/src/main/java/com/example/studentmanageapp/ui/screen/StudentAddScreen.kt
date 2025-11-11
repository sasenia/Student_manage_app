package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.viewmodel.StudentViewModel

import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Delete

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentEditScreen(
    student: Student,
    viewModel: StudentViewModel,
    navController: NavHostController
) {
    var studentId by rememberSaveable { mutableStateOf("") }
    var studentName by rememberSaveable { mutableStateOf("") }
    var studentGender by rememberSaveable { mutableStateOf("남") }

    val studentList by viewModel.studentList.collectAsState()
    val selectedIds = remember { mutableStateListOf<Int>() }
    var showDeleteDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("학생 관리") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                actions = {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "삭제 모드")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ... 생략된 import 및 Scaffold는 동일 ...

            OutlinedTextField(
                value = studentId,
                onValueChange = { studentId = it },
                label = { Text("학생 번호") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = studentName,
                onValueChange = { studentName = it },
                label = { Text("학생 이름") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text("성별 선택", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                GenderRadio("남", studentGender) { studentGender = it }
                GenderRadio("여", studentGender) { studentGender = it }
            }

            val id = studentId.toIntOrNull()
            val isExisting = id != null && studentList.any { it.id == id }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        if (id != null && studentName.isNotBlank() && !isExisting) {
                            val newStudent = Student(
                                id = id,
                                name = studentName.trim(),
                                gender = studentGender,
                                memo = "",
                                praiseScore = 0,
                                activityMap = emptyMap()
                            )
                            viewModel.addStudent(newStudent)
                            studentId = ""
                            studentName = ""
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = id != null && !isExisting
                ) {
                    Text("학생 추가")
                }

                Button(
                    onClick = {
                        if (id != null && studentName.isNotBlank() && isExisting) {
                            val updatedStudent = Student(
                                id = id,
                                name = studentName.trim(),
                                gender = studentGender,
                                memo = "",
                                praiseScore = 0,
                                activityMap = emptyMap()
                            )
                            viewModel.updateStudent(updatedStudent)
                            studentId = ""
                            studentName = ""
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = id != null && isExisting
                ) {
                    Text("학생 수정")
                }
            }

            Divider()

            Text("현재 등록된 학생", style = MaterialTheme.typography.titleMedium)

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(studentList.sortedBy { it.id }) { item ->
                    StudentItemRow(
                        student = item,
                        isDeleteMode = false,
                        isSelected = false,
                        onSelect = {},
                        onEdit = {
                            studentId = item.id.toString()
                            studentName = item.name
                            studentGender = item.gender
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                confirmButton = {
                    Button(onClick = {
                        selectedIds.forEach { id ->
                            studentList.find { it.id == id }?.let { viewModel.deleteStudent(it) }
                        }
                        selectedIds.clear()
                        showDeleteDialog = false
                    }) {
                        Text("선택된 학생 삭제")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("취소")
                    }
                },
                title = { Text("학생 삭제") },
                text = {
                    LazyColumn {
                        items(studentList.sortedBy { it.id }) { item ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedIds.contains(item.id),
                                    onCheckedChange = { checked ->
                                        if (checked) selectedIds.add(item.id)
                                        else selectedIds.remove(item.id)
                                    }
                                )
                                Text("${item.id} - ${item.name}")
                            }
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun StudentItemRow(
    student: Student,
    isDeleteMode: Boolean,
    isSelected: Boolean,
    onSelect: (Boolean) -> Unit,
    onEdit: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        tonalElevation = 1.dp,
        shape = MaterialTheme.shapes.extraSmall
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isDeleteMode) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = onSelect,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Text(
                    text = "${student.id}",
                    modifier = Modifier.width(40.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = student.name,
                    modifier = Modifier.width(80.dp),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = student.gender,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "수정",
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
@Composable
fun GenderRadio(label: String, selected: String, onSelect: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(
            selected = selected == label,
            onClick = { onSelect(label) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(label)
    }
}