package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.viewmodel.StudentViewModel

@Composable
fun AttendanceScreen(
    viewModel: StudentViewModel,
    attendanceRecords: SnapshotStateList<AttendanceRecord>
) {
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
            .padding(16.dp)
    ) {
        Text(
            text = "출석 체크",
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(16.dp))

        studentList.forEach { student ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = student.name)
                Checkbox(
                    checked = student.attendance,
                    onCheckedChange = { isChecked ->
                        val updated = student.copy(attendance = isChecked)
                        viewModel.updateStudent(updated)
                    }
                )
            }
        }
    }
}