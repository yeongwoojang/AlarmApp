package com.example.couroutinstudy.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.couroutinstudy.model.vo.Alarm

@Dao
interface AlarmDao {

    @Query("SELECT *FROM ALARM")
    fun getAll() : LiveData<List<Alarm>>

    @Insert
    suspend fun insert(alarm: Alarm)
    @Query("DELETE FROM ALARM")
    suspend fun deleteAll() : Void

    @Delete
    suspend fun delete(alarm : Alarm)
}