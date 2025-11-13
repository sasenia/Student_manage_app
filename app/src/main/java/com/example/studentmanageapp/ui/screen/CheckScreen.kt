package com.example.studentmanageapp.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.studentmanageapp.model.AttendanceRecord
import com.example.studentmanageapp.model.AttendanceStatus
import com.example.studentmanageapp.model.toKorean
import com.example.studentmanageapp.viewmodel.StudentViewModel
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

@Composable
fun CheckScreen(viewModel: StudentViewModel, navController: NavController) {
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    val selectedStatus = remember { mutableStateOf<AttendanceStatus?>(null) }
    val filteredList = remember { mutableStateListOf<AttendanceRecord>() }

    LaunchedEffect(currentMonth, selectedStatus.value) {
        viewModel.getFilteredMonthlyAttendance(currentMonth) { records ->
            val result = records
                .filter { selectedStatus.value == null || it.status == selectedStatus.value }
                .sortedWith(compareBy({ it.studentId }, { it.date }))
            filteredList.clear()
            filteredList.addAll(result)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("출석 통계", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 월 변경 UI (정렬 개선)
        Box(modifier = Modifier.fillMaxWidth()) {
            IconButton(
                onClick = { currentMonth = currentMonth.minusMonths(1) },
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "이전 달")
            }

            Text(
                "${currentMonth.year}년 ${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.KOREAN)}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = { currentMonth = currentMonth.plusMonths(1) },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Default.ArrowForward, contentDescription = "다음 달")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 필터 버튼
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { selectedStatus.value = null }) { Text("전체") }
            Button(onClick = { selectedStatus.value = AttendanceStatus.ABSENT }) { Text("결석") }
            Button(onClick = { selectedStatus.value = AttendanceStatus.LATE }) { Text("지각") }
            Button(onClick = { selectedStatus.value = AttendanceStatus.LEAVE }) { Text("조퇴") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ 헤더
        Row(modifier = Modifier.fillMaxWidth()) {
            Text("번호", modifier = Modifier.width(60.dp), style = MaterialTheme.typography.labelMedium)
            Text("이름", modifier = Modifier.width(100.dp), style = MaterialTheme.typography.labelMedium)
            Text("날짜", modifier = Modifier.width(120.dp), style = MaterialTheme.typography.labelMedium)
            Text("상태", modifier = Modifier.weight(1f), style = MaterialTheme.typography.labelMedium)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ 출석 리스트
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(filteredList) { record ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text("${record.studentId}", modifier = Modifier.width(60.dp))
                    Text(record.studentName, modifier = Modifier.width(100.dp))
                    Text(record.date.toString(), modifier = Modifier.width(120.dp))
                    Text(record.status.toKorean(), modifier = Modifier.weight(1f))
                }
            }
        }
    }
}