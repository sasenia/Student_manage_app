package com.example.studentmanageapp.data.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class NestedMapConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromNestedMap(value: Map<String, Map<String, String>>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toNestedMap(value: String): Map<String, Map<String, String>> {
        return try {
            val type = object : TypeToken<Map<String, Map<String, String>>>() {}.type
            gson.fromJson(value, type)
        } catch (e: Exception) {
            // 예전 형식 대응: 단일 문자열을 기본 구조로 감싸기
            mapOf("기본과제" to mapOf("1970-01-01" to value))
        }
    }
}