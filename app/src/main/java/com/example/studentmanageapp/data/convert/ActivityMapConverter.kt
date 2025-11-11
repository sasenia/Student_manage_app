package com.example.studentmanageapp.data.converter

import androidx.room.TypeConverter

class ActivityMapConverter {

    // Map<String, Map<String, String>> → String
    @TypeConverter
    fun fromNestedMap(map: Map<String, Map<String, String>>?): String {
        return map?.entries?.joinToString("|") { (activity, dateMap) ->
            val inner = dateMap.entries.joinToString(";") { "${it.key}:${it.value}" }
            "$activity{$inner}"
        } ?: ""
    }

    // String → Map<String, Map<String, String>>
    @TypeConverter
    fun toNestedMap(data: String): Map<String, Map<String, String>> {
        if (data.isBlank()) return emptyMap()

        return data.split("|").mapNotNull { entry ->
            val activitySplit = entry.split("{", "}")
            if (activitySplit.size == 3) {
                val activity = activitySplit[0]
                val innerData = activitySplit[1]
                val dateMap = innerData.split(";").mapNotNull {
                    val parts = it.split(":")
                    if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap()
                activity to dateMap
            } else null
        }.toMap()
    }
}