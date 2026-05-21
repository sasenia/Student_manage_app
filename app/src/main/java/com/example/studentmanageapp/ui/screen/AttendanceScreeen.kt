package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.model.AttendanceStatus
import com.example.studentmanageapp.viewmodel.StudentViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth


// =========================================================
// 1️⃣ 공통 색상 정의
// =========================================================
val PresentColor = Color(0xFF65B018)
val AbsentColor = Color(0xFFFC6B05)
val LateColor = Color(0xFFFFB62B)
val LeaveColor = Color(0xFF99D8D8)

// =========================================================
// 2️⃣ 달력 관련 Composable
// =========================================================
@Composable
fun CalendarDayCell(
    date: LocalDate,
    isMarked: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) Color(0xFFB3E5FC) else Color.Transparent

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall
            )

            if (isMarked) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(PresentColor)
                )
            }
        }
    }
}

@Composable
fun CustomCalendar(
    currentMonth: YearMonth,
    selectedDate: LocalDate,
    markedDates: List<LocalDate>,
    onDateSelected: (LocalDate) -> Unit,
    onMonthChange: (YearMonth) -> Unit
) {
    var selectedYear by remember { mutableStateOf(currentMonth.year) }

    Column(modifier = Modifier.fillMaxWidth()) {

        // 연도 선택
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { selectedYear-- }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "이전 년도")
            }
            Text("${selectedYear}년", style = MaterialTheme.typography.titleMedium)
            IconButton(onClick = { selectedYear++ }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "다음 년도")
            }
        }

        // 월 선택
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(12) { index ->
                val month = index + 1
                val isCurrent =
                    currentMonth.year == selectedYear && currentMonth.monthValue == month

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCurrent) Color(0xFF90CAF9) else Color(0xFFE0E0E0))
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) { onMonthChange(YearMonth.of(selectedYear, month)) }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${month}월")
                }
            }
        }

        // 날짜 계산
        val firstDayOfMonth = currentMonth.atDay(1)
        val startOffset = firstDayOfMonth.dayOfWeek.value % 7
        val totalDays = startOffset + currentMonth.lengthOfMonth()

        val days = (0 until totalDays).map { index ->
            if (index < startOffset) null
            else firstDayOfMonth.plusDays((index - startOffset).toLong())
        }

        // 날짜 그리드
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF8E1)),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(days.size) { index ->
                val date = days[index]
                if (date != null) {
                    CalendarDayCell(
                        date = date,
                        isMarked = markedDates.contains(date),
                        isSelected = date == selectedDate,
                        onClick = { onDateSelected(date) }
                    )
                } else {
                    Spacer(modifier = Modifier.aspectRatio(1f))
                }
            }
        }
    }
}

// =========================================================
// 3️⃣ 출석 화면
// =========================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    selectedDate: LocalDate,
    viewModel: StudentViewModel,
    attendanceRecords: SnapshotStateList<AttendanceRecord>,
    navController: NavController
) {
    val studentList by viewModel.studentList.collectAsState()
    val sortedStudents = studentList.sortedBy { it.id }

    val attendanceMap = remember { mutableStateMapOf<Int, AttendanceStatus?>() }
    val markedDates = remember { mutableStateListOf<LocalDate>() }

    var currentDate by remember { mutableStateOf(selectedDate) }
    var showCalendar by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val statusColors = mapOf(
        AttendanceStatus.PRESENT to Color(0xFF4CAF50),
        AttendanceStatus.ABSENT to Color(0xFFF44336),
        AttendanceStatus.LATE to Color(0xFFFF9800),
        AttendanceStatus.LEAVE to Color(0xFF2196F3)
    )

    val statusLabels = mapOf(
        AttendanceStatus.PRESENT to "출석",
        AttendanceStatus.ABSENT to "결석",
        AttendanceStatus.LATE to "지각",
        AttendanceStatus.LEAVE to "조퇴"
    )

    LaunchedEffect(currentDate) {
        viewModel.getAttendanceByDate(currentDate) { records ->
            attendanceMap.clear()
            records.forEach { attendanceMap[it.studentId] = it.status }
        }

        viewModel.getAttendanceByMonth(YearMonth.from(currentDate).toString()) { records ->
            markedDates.clear()
            markedDates.addAll(records.filter { it.status != AttendanceStatus.PRESENT }
                .map { it.date })
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        // 상단 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("출석 체크", style = MaterialTheme.typography.titleLarge)
                Text("날짜: $currentDate")
            }
            Row {


                // ✅ 추가: 출결 초기화(선택했던 상태를 null로)
                IconButton(
                    onClick = {
                        viewModel.deleteAttendanceByDate(currentDate) {

                            // UI도 즉시 초기화
                            sortedStudents.forEach { student ->
                                attendanceMap[student.id] = null
                            }

                            // 달력 표시도 갱신
                            viewModel.getAttendanceByMonth(
                                YearMonth.from(currentDate).toString()
                            ) { records ->
                                markedDates.clear()
                                markedDates.addAll(
                                    records.filter { it.status != AttendanceStatus.PRESENT }
                                        .map { it.date }
                                )
                            }
                        }
                    }
                ) {
                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "출결 초기화")
                }

                IconButton(onClick = { navController.navigate("checkScreen") }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "출석 확인")
                }

                IconButton(onClick = { showCalendar = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "달력")
                }
            }

        }

        // 월 이동
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentDate = currentDate.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = null)
            }
            Text(
                "${currentDate.year}년 ${currentDate.monthValue}월",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { currentDate = currentDate.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = null)
            }
        }

        // 달력 다이얼로그
        if (showCalendar) {
            Dialog(onDismissRequest = { showCalendar = false }) {
                Surface(shape = RoundedCornerShape(12.dp), color = Color(0xFFFFF8E1)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CustomCalendar(
                            currentMonth = YearMonth.from(currentDate),
                            selectedDate = currentDate,
                            markedDates = markedDates,
                            onDateSelected = {
                                currentDate = it
                                showCalendar = false
                            },
                            onMonthChange = {
                                currentDate = it.atDay(1)
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 학생 출석 리스트
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sortedStudents) { student ->
                val currentStatus = attendanceMap[student.id]

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${student.id}", modifier = Modifier.weight(0.7f))
                    Text(student.name, modifier = Modifier.weight(1f))

                    Row(
                        modifier = Modifier.weight(3f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AttendanceStatus.values().forEach { status ->
                            val selected = currentStatus == status
                            val bgColor = if (selected) statusColors[status]!! else Color.LightGray

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(bgColor)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() }
                                    ) {
                                        attendanceMap[student.id] = status
                                        coroutineScope.launch {
                                            viewModel.saveAttendance(
                                                AttendanceRecord(
                                                    studentId = student.id,
                                                    studentName = student.name,
                                                    date = currentDate,
                                                    status = status
                                                )
                                            )
                                        }
                                    }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    statusLabels[status]!!,
                                    color = if (selected) Color.White else Color.Black,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
