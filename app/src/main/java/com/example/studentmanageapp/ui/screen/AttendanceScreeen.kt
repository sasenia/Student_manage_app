package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studentmanageapp.data.entity.Student
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.model.AttendanceStatus
import com.example.studentmanageapp.viewmodel.StudentViewModel
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import kotlinx.coroutines.launch
import java.time.LocalDate
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.window.Dialog
import java.time.YearMonth
import java.time.DayOfWeek
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow


val PresentColor = Color(0xFF65B018)
val AbsentColor = Color(0xFFFC6B05)
val LateColor = Color(0xFFFFB62B)
val LeaveColor = Color(0xFF99D8D8)


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
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
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

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(12) { monthIndex ->
                val monthValue = monthIndex + 1
                val isCurrentMonth =
                    currentMonth.monthValue == monthValue && currentMonth.year == selectedYear

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isCurrentMonth) Color(0xFF90CAF9) else Color(0xFFE0E0E0))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = rememberRipple(),
                            onClick = {
                                val newMonth = YearMonth.of(selectedYear, monthValue)
                                onMonthChange(newMonth)
                            }
                        )
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${monthValue}월", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        val firstDayOfMonth = currentMonth.atDay(1)
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7
        val totalDays = firstDayOfWeek + currentMonth.lengthOfMonth()
        val days = (0 until totalDays).map { index ->
            if (index < firstDayOfWeek) null else firstDayOfMonth.plusDays((index - firstDayOfWeek).toLong())
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFFF8E1)),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(days.size) { index ->
                val date = days[index]
                if (date != null) {
                    CalendarDayCell(
                        date = date,
                        isMarked = markedDates.any { it.isEqual(date) },
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    selectedDate: LocalDate,
    viewModel: StudentViewModel,
    attendanceRecords: SnapshotStateList<AttendanceRecord>,
    navController: NavController
) {
    val studentList by viewModel.studentList.collectAsState()
    val sortedList = studentList.sortedBy { it.id }
    val attendanceMap = remember { mutableStateMapOf<Int, AttendanceStatus>() }
    val coroutineScope = rememberCoroutineScope()
    var currentDate by remember { mutableStateOf(selectedDate) }
    var showCalendar by remember { mutableStateOf(false) }
    val markedDates = remember { mutableStateListOf<LocalDate>() }

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
        val yearMonth = YearMonth.from(currentDate).toString()
        viewModel.getAttendanceByDate(currentDate) { records ->
            attendanceMap.clear()
            records.forEach { record ->
                attendanceMap[record.studentId] = record.status
            }
        }
        viewModel.getAttendanceByMonth(yearMonth) { records ->
            markedDates.clear()
            markedDates.addAll(
                records.filter { it.status != AttendanceStatus.PRESENT }
                    .map { it.date }
            )
        }
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("출석 체크", style = MaterialTheme.typography.titleLarge)
                Text("날짜: $currentDate", style = MaterialTheme.typography.bodyMedium)
            }
            Row {
                IconButton(onClick = { navController.navigate("checkScreen") }) {
                    Icon(Icons.Default.CheckCircle, contentDescription = "출석 확인")
                }
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { showCalendar = !showCalendar }) {
                    Icon(Icons.Default.DateRange, contentDescription = "달력 열기")
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentDate = currentDate.minusMonths(1) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "이전 달")
            }
            Text(
                "${currentDate.year}년 ${currentDate.monthValue}월",
                style = MaterialTheme.typography.titleMedium
            )
            IconButton(onClick = { currentDate = currentDate.plusMonths(1) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "다음 달")
            }
        }

        if (showCalendar) {
            Dialog(onDismissRequest = { showCalendar = false }) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8E1),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        CustomCalendar(
                            currentMonth = YearMonth.from(currentDate),
                            selectedDate = currentDate,
                            markedDates = markedDates,
                            onDateSelected = {
                                currentDate = it
                                showCalendar = false
                            },
                            onMonthChange = { newMonth ->
                                currentDate = newMonth.atDay(1)
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (markedDates.contains(currentDate)) {
                            Text(
                                text = "● 이 날짜에는 결석/지각/조퇴 기록이 있습니다.",
                                color = PresentColor,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                }
            }
        }

        // ✅ 상단 라벨 추가
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "번호",
                modifier = Modifier.weight(0.8f),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "이름",
                modifier = Modifier.weight(1.2f),
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = "출석현황",
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center // ✅ 오른쪽 정렬
            )

        }

        Spacer(modifier = Modifier.height(12.dp)) // ✅ 줄 없이 여백만

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(sortedList) { student ->
                val currentStatus = attendanceMap[student.id] ?: AttendanceStatus.PRESENT

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 12.dp), // ✅ 오른쪽 여백 확보
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween // ✅ 줄 정렬 유지
                ) {
                    Text(
                        text = "${student.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(0.8f)
                    )
                    Text(
                        text = student.name,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .weight(1f) // ✅ 살짝 줄임
                            .padding(start = 4.dp) // ✅ 번호와 이름 사이 좁힘
                    )
                    Column(
                        modifier = Modifier
                            .weight(3f)
                            .padding(start = 8.dp), // ✅ 버튼을 살짝 왼쪽으로
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            AttendanceStatus.values().forEach { status ->
                                val isSelected = currentStatus == status
                                val backgroundColor = if (isSelected) statusColors[status] ?: Color.Gray else Color.LightGray
                                val textColor = if (isSelected) Color.White else Color.Black

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(backgroundColor)
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null,
                                            onClick = {
                                                attendanceMap[student.id] = status
                                                val record = AttendanceRecord(
                                                    studentId = student.id,
                                                    studentName = student.name,
                                                    date = currentDate,
                                                    status = status
                                                )
                                                coroutineScope.launch {
                                                    viewModel.saveAttendance(record)
                                                    val yearMonth = YearMonth.from(currentDate).toString()
                                                    viewModel.getAttendanceByMonth(yearMonth) { records ->
                                                        markedDates.clear()
                                                        markedDates.addAll(
                                                            records.filter { it.status != AttendanceStatus.PRESENT }
                                                                .map { it.date }
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = statusLabels[status] ?: "",
                                        color = textColor,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}