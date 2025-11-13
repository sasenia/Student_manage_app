package com.example.studentmanageapp.model

import androidx.room.TypeConverter

enum class AttendanceStatus {
    PRESENT,
    ABSENT,
    LATE,
    LEAVE;

    companion object {
        fun from(value: String): AttendanceStatus {
            return values().find { it.name == value } ?: PRESENT
        }
    }
}

class AttendanceStatusConverter {
    @TypeConverter
    fun fromStatus(status: AttendanceStatus): String {
        return status.name
    }

    @TypeConverter
    fun toStatus(value: String): AttendanceStatus {
        return AttendanceStatus.from(value)
    }
}

fun AttendanceStatus.toKorean(): String = when (this) {
    AttendanceStatus.ABSENT -> "결석"
    AttendanceStatus.LATE -> "지각"
    AttendanceStatus.LEAVE -> "조퇴"
    AttendanceStatus.PRESENT -> "출석"
}