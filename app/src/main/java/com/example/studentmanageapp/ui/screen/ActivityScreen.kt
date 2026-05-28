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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studentmanageapp.data.entity.Activity
import com.example.studentmanageapp.viewmdel.ActivityUiViewModel
import com.example.studentmanageapp.viewmodel.ActivityViewModel
import com.example.studentmanageapp.viewmodel.ActivityViewModelFactory
import com.example.studentmanageapp.viewmodel.StudentViewModel
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    studentViewModel: StudentViewModel,
    activityUiViewModel: ActivityUiViewModel
) {
    val context = LocalContext.current
    val activityViewModel: ActivityViewModel =
        viewModel(factory = ActivityViewModelFactory(context))

    val studentList by studentViewModel.studentList.collectAsState()
    val activityList by activityViewModel.activityList.collectAsState()

    val uiState = activityUiViewModel.uiState
    val currentDate = uiState.selectedDate

    // ✅ 선택된 활동은 "로컬 remember"가 아니라 ViewModel 상태로부터 복원
    val selectedActivity = activityList.find { it.name == uiState.selectedActivityName }

    var showActivityDialog by remember { mutableStateOf(false) }     // UI는 기존 그대로
    val selectedActivities = remember { mutableStateListOf<Activity>() }
    var showCalendar by remember { mutableStateOf(false) }           // UI는 기존 그대로

    val year = currentDate.year
    val month = currentDate.monthValue - 1
    val day = currentDate.dayOfMonth

    Column(modifier = Modifier.padding(16.dp)) {

        /* ================= 상단 헤더 ================= */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "참여 확인",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            Text(currentDate.toString(), style = MaterialTheme.typography.bodyMedium)

            IconButton(onClick = { showCalendar = true }) {
                Icon(Icons.Default.DateRange, contentDescription = "날짜 선택")
            }

            IconButton(onClick = { showActivityDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "활동 관리")
            }
        }

        /* ================= 날짜 선택 ================= */
        if (showCalendar) {
            LaunchedEffect(Unit) {
                DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        activityUiViewModel.selectDate(LocalDate.of(y, m + 1, d))
                        showCalendar = false
                    },
                    year, month, day
                ).show()
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        /* ================= 활동 선택 ================= */
        var expanded by remember { mutableStateOf(false) } // 기존 그대로

        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedActivity?.name ?: "활동 선택")
            }

            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                activityList.forEach { activity ->
                    DropdownMenuItem(
                        text = { Text(activity.name) },
                        onClick = {
                            // ✅ 선택값을 ViewModel에 저장 (이게 핵심)
                            activityUiViewModel.selectActivity(activity.name)
                            expanded = false
                        }
                    )
                }
            }
        }

        /* ================= 활동 평가 ================= */
        if (selectedActivity != null) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(modifier = Modifier.fillMaxWidth()) {

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("번호", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("이름", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                        Text("평가", modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                    }
                }

                items(studentList.sortedBy { it.id }) { student ->

                    val isLastUpdated =
                        student.id == studentViewModel.lastUpdatedStudentId

                    val key = "${selectedActivity.name}-${currentDate}"
                    val selectedLevel = student.activityMap[key]
                    var levelExpanded by remember { mutableStateOf(false) }

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
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )

                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            OutlinedButton(
                                onClick = { levelExpanded = true },
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(selectedLevel ?: " ")
                            }

                            DropdownMenu(
                                expanded = levelExpanded,
                                onDismissRequest = { levelExpanded = false },
                                modifier = Modifier.width(56.dp)
                            ) {
                                listOf("상", "중", "하", "O", "X").forEach { level ->
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
                                            levelExpanded = false
                                            val updated = student.activityMap.toMutableMap()
                                            updated[key] = level
                                            studentViewModel.saveActivity(
                                                student.id,
                                                selectedActivity.name,
                                                level,
                                                currentDate.toString()
                                            )
                                            studentViewModel.markStudentUpdated(student.id)
                                        },
                                        contentPadding = PaddingValues(0.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        /* ================= 활동 관리 다이얼로그 ================= */
        if (showActivityDialog) {
            Dialog(onDismissRequest = { showActivityDialog = false }) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = Color(0xFFFFF8E1)
                ) {
                    var newActivityName by rememberSaveable { mutableStateOf("") }

                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("활동명 관리", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = newActivityName,
                                onValueChange = { newActivityName = it },
                                modifier = Modifier.weight(1f),
                                label = { Text("활동명") },
                                singleLine = true,
                                colors = TextFieldDefaults.textFieldColors(
                                    containerColor = Color(0xFFFFF8E1)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                if (newActivityName.isNotBlank()) {
                                    activityViewModel.addActivity(newActivityName)
                                    newActivityName = ""
                                }
                            }) {
                                Text("추가")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        activityList.forEach { activity ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = selectedActivities.contains(activity),
                                    onCheckedChange = {
                                        if (it) selectedActivities.add(activity)
                                        else selectedActivities.remove(activity)
                                    }
                                )
                                Text(activity.name)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Button(
                                onClick = {
                                    activityViewModel.deleteActivities(selectedActivities.map { it.name })
                                    if (selectedActivities.any { it.name == uiState.selectedActivityName }) {
                                        activityUiViewModel.clearSelectedActivity()
                                    }
                                    selectedActivities.clear()
                                },
                                enabled = selectedActivities.isNotEmpty()
                            ) {
                                Text("선택 삭제")
                            }

                            TextButton(onClick = { showActivityDialog = false }) {
                                Text("닫기")
                            }
                        }
                    }
                }
            }
        }
    }
}
