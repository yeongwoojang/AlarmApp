package com.example.couroutinstudy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.couroutinstudy.model.vo.Alarm

@Dao
interface AlarmDao {

    @Query("SELECT *FROM ALARM")
    fun getAll() : LiveData<List<Alarm>>

    @Insert
    suspend fun insert(alarm: Alarm)

    @Query("UPDATE ALARM SET isRepeat = :isRepeat WHERE id =:alarmId")
    suspend fun updateIsRepeat(isRepeat : Boolean, alarmId : Int)

    @Query("UPDATE ALARM SET isOn = :onOff WHERE id =:alarmId")
    suspend fun updateOnOff(onOff : Boolean, alarmId : Int)

    @Query("DELETE FROM ALARM")
    suspend fun deleteAll() : Void

    @Delete
    suspend fun delete(alarm : Alarm)
}