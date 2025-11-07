package com.example.studentmanageapp.data.dao

import androidx.room.*
import com.example.studentmanageapp.data.entity.Student

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(student: Student)

    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students ORDER BY name ASC")
    suspend fun getAll(): List<Student>

    @Query("DELETE FROM students WHERE id = :id")
    suspend fun deleteById(id: Int)
}