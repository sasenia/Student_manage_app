package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.studentmanageapp.data.entity.Activity
import com.example.studentmanageapp.viewmodel.StudentViewModel
import com.example.studentmanageapp.viewmodel.ActivityViewModel
import com.example.studentmanageapp.viewmodel.ActivityViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalDate
import android.app.DatePickerDialog
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import java.util.Calendar


// 활동 평가 UI
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityScreen(
    studentViewModel: StudentViewModel
) {
    val context = LocalContext.current
    val activityViewModel: ActivityViewModel =
        viewModel(factory = ActivityViewModelFactory(context))
    val studentList by studentViewModel.studentList.collectAsState()
    val activityList by activityViewModel.activityList.collectAsState()

    var selectedActivity by remember { mutableStateOf<Activity?>(null) }
    var showActivityDialog by remember { mutableStateOf(false) }
    var newActivityName by remember { mutableStateOf("") }
    val selectedActivities = remember { mutableStateListOf<Activity>() }

    var currentDate by remember { mutableStateOf(LocalDate.now()) }
    var showCalendar by remember { mutableStateOf(false) }

    val year = currentDate.year
    val month = currentDate.monthValue - 1 // Calendar는 0부터 시작
    val day = currentDate.dayOfMonth

    Column(modifier = Modifier.padding(16.dp)) {

        // 상단 제목 + 날짜 + 아이콘
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "참여 확인",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$currentDate",
                    style = MaterialTheme.typography.bodyMedium
                )
                IconButton(onClick = { showCalendar = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "달력 열기")
                }

                IconButton(onClick = { showActivityDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "활동 추가")
                }
            }
        }
        if (showCalendar) {
            LaunchedEffect(Unit) {
                DatePickerDialog(
                    context,
                    { _, selectedYear, selectedMonth, selectedDay ->
                        currentDate =
                            LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                        showCalendar = false
                    },
                    year,
                    month,
                    day
                ).show()
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // 활동 선택 드롭다운
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }) {
                Text(selectedActivity?.name ?: "활동 선택")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                activityList.forEach { activity ->
                    DropdownMenuItem(
                        text = { Text(activity.name) },
                        onClick = {
                            selectedActivity = activity
                            expanded = false
                        }
                    )
                }
            }
        }

        // 활동 평가 UI
        if (selectedActivity != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("활동 평가", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp) // ✅ 간격 줄임
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp), // ✅ 라벨 행도 간격 축소
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("번호", modifier = Modifier.width(60.dp), style = MaterialTheme.typography.labelMedium)
                        Text("이름", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.labelMedium)
                        Text("평가", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
                    }
                }

                items(studentList.sortedBy { it.id }) { student ->
                    val key = "${selectedActivity!!.name}-${currentDate}"
                    val selectedLevel = student.activityMap[key] ?: "상"
                    var levelExpanded by remember { mutableStateOf(false) }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp), // ✅ 학생 간 간격 축소
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            "${student.id}",
                            modifier = Modifier.width(60.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            student.name,
                            modifier = Modifier.width(100.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedButton(
                                onClick = { levelExpanded = true },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(selectedLevel, style = MaterialTheme.typography.bodyMedium)
                            }

                            DropdownMenu(
                                expanded = levelExpanded,
                                onDismissRequest = { levelExpanded = false }
                            ) {
                                listOf("상", "중", "하", "O", "X").forEach { level ->
                                    DropdownMenuItem(
                                        text = { Text(level, style = MaterialTheme.typography.bodyMedium) },
                                        onClick = {
                                            levelExpanded = false
                                            val updatedMap = student.activityMap.toMutableMap()
                                            updatedMap[key] = level
                                            studentViewModel.updateStudent(student.copy(activityMap = updatedMap))
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }


        // ✅ 팝업은 항상 렌더링되도록 조건문 바깥에 위치
        if (showActivityDialog) {
            Dialog(onDismissRequest = { showActivityDialog = false }) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 6.dp,
                    color = Color(0xFFFFF8E1)
                ) {
                    var newActivityName by rememberSaveable { mutableStateOf("") }

                    val scrollState = rememberScrollState()

                    Column(
                        modifier = Modifier
                            .padding(24.dp)
                            .verticalScroll(scrollState) // ✅ 스크롤 가능하게 처리
                    ) {
                        Text("활동명 관리", style = MaterialTheme.typography.titleLarge)

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            TextField(
                                value = newActivityName,
                                onValueChange = { newActivityName = it },
                                label = { Text("활동명") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
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
                                    onCheckedChange = { checked ->
                                        if (checked) selectedActivities.add(activity)
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
                                    if (selectedActivity in selectedActivities) selectedActivity =
                                        null
                                    selectedActivities.clear()
                                },
                                enabled = selectedActivities.isNotEmpty()
                            ) {
                                Text("선택한 활동 삭제")
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