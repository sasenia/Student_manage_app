package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanageapp.data.entity.Subject
import com.example.studentmanageapp.viewmodel.SubjectViewModel

@Composable
fun SubjectEditScreen(viewModel: SubjectViewModel = viewModel()) {
    val subjectList by viewModel.subjectList.collectAsState()
    var newSubjectName by remember { mutableStateOf("") }
    val selectedSubjects = remember { mutableStateListOf<Subject>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("과목 수정", style = MaterialTheme.typography.titleLarge)

        // ✅ 과목 입력란
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = newSubjectName,
                onValueChange = { newSubjectName = it},
                label = { Text("새 과목명") },
                singleLine = false,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                modifier = Modifier.weight(1f)
            )
            Button(onClick = {
                if (newSubjectName.isNotBlank()) {
                    viewModel.addSubject(newSubjectName)
                    newSubjectName = ""
                }
            }) {
                Text("추가")
            }
        }

        Divider()

        // ✅ 과목 리스트 출력 + 체크박스
        Text("과목 목록", style = MaterialTheme.typography.titleMedium)
        subjectList.forEach { subject ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = selectedSubjects.contains(subject),
                    onCheckedChange = { checked ->
                        if (checked) selectedSubjects.add(subject)
                        else selectedSubjects.remove(subject)
                    }
                )
                Text(subject.name, style = MaterialTheme.typography.bodyLarge)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ✅ 삭제 버튼
        Button(
            onClick = {
                viewModel.deleteSubjects(selectedSubjects.map { it.name })
                selectedSubjects.clear()
            },
            enabled = selectedSubjects.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("선택한 과목 삭제")
        }
    }
}