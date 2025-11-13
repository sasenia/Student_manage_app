package com.example.studentmanageapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.studentmanageapp.model.AttendanceStatus
import com.example.studentmanageapp.model.AttendanceStatusConverter
import com.example.studentmanageapp.data.converter.MapConverter
import com.example.studentmanageapp.data.converter.NestedMapConverter // ✅ 추가

@Entity(tableName = "students")
@TypeConverters(
    AttendanceStatusConverter::class,
    MapConverter::class,
    NestedMapConverter::class // ✅ 중첩 Map 컨버터 등록
)
data class Student(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val gender: String,
    val memo: String = "",

    val attendanceStatus: AttendanceStatus = AttendanceStatus.PRESENT,

    // ✅ 과제명 → 날짜(yyyy-MM-dd) → 평가("상", "중", "하")
    val homeworkMap: Map<String, Map<String, String>> = emptyMap(),

    // ✅ 과제명 → 날짜(yyyy-MM-dd) → 메모 내용
    val memoMap: Map<String, Map<String, String>> = emptyMap(),

    val praise: String = "",
    val activityMap: Map<String, String> = emptyMap(),
    val praiseScore: Int = 0
)