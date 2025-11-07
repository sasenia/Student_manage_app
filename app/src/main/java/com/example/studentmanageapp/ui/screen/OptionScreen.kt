package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsScreen(viewModel: StudentViewModel) {
    var selectedOption by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("옵션", style = MaterialTheme.typography.titleLarge)

        if (selectedOption != "학생 관리") {
            Button(
                onClick = { selectedOption = "학생 관리" },
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(70.dp)
            ) {
                Text("학생 관리", fontSize = 20.sp)
            }
        }

        if (selectedOption == "학생 관리") {
            Spacer(modifier = Modifier.height(16.dp))
            StudentManagementSection(viewModel = viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentManagementSection(viewModel: StudentViewModel) {
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("남") }
    var memo by remember { mutableStateOf("") }
    var studentList by remember { mutableStateOf<List<Student>>(emptyList()) }
    var studentId by remember { mutableStateOf("") }

    val inputColors = TextFieldDefaults.colors(
        focusedContainerColor = Color(0xFFE8F5E9),       // 연한 연두색
        unfocusedContainerColor = Color(0xFFE8F5E9),     // 연한 연두색
        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
        unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
    )

    LaunchedEffect(Unit) {
        viewModel.getAllStudents { result ->
            studentList = result
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("학생 추가", fontSize = 20.sp)

        Text("학생 번호", fontSize = 20.sp)
        TextField(
            value = studentId,
            onValueChange = { studentId = it },
            label = { Text("번호") },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(60.dp),
            colors = inputColors
        )

        Text("이름", fontSize = 20.sp)
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("이름") },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(60.dp),
            colors = inputColors
        )

        Text("성별", fontSize = 20.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            GenderRadio("남", gender) { gender = it }
            GenderRadio("여", gender) { gender = it }
        }

        Text("메모", fontSize = 20.sp)
        TextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("메모") },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(60.dp),
            colors = inputColors
        )

        Button(
            onClick = {
                val id = studentId.toIntOrNull()
                if (id != null && name.isNotBlank()) {
                    val newStudent = Student(
                        id = id,
                        name = name.trim(),
                        gender = gender,
                        memo = memo
                    )
                    viewModel.addStudent(newStudent)
                    viewModel.getAllStudents { studentList = it }
                    studentId = ""; name = ""; gender = "남"; memo = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(55.dp)
        ) {
            Text("학생 추가", fontSize = 18.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text("학생 삭제", fontSize = 20.sp)

        studentList.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${student.id}  ${student.name}  ${student.gender}  ${student.memo}")
                Button(
                    onClick = {
                        viewModel.deleteStudent(student)
                        viewModel.getAllStudents { studentList = it }
                    },
                    modifier = Modifier.height(40.dp)
                ) {
                    Text("삭제")
                }
            }
        }
    }
}

@Composable
fun GenderRadio(label: String, selected: String, onSelect: (String) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(end = 12.dp)
            .height(48.dp)
    ) {
        RadioButton(
            selected = selected == label,
            onClick = { onSelect(label) },
            modifier = Modifier.scale(1.4f),
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSecondary,
                disabledSelectedColor = MaterialTheme.colorScheme.surface
            )
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )
    }
}