package com.example.studentmanageapp.data.dao

import androidx.room.*
import com.example.studentmanageapp.data.entity.Activity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities")
    fun getAllActivities(): Flow<List<Activity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(activity: Activity)

    @Delete
    suspend fun delete(activity: Activity)

    @Query("DELETE FROM activities WHERE name IN (:names)")
    suspend fun deleteByNames(names: List<String>)
}