package com.example.studentmanageapp.data.converter

import androidx.room.TypeConverter
import com.example.studentmanageapp.model.AttendanceStatus

class AttendanceStatusConverter {
    @TypeConverter
    fun fromStatus(status: AttendanceStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): AttendanceStatus = AttendanceStatus.valueOf(value)
}