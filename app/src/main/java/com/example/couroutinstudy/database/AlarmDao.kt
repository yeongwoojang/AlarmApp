package com.example.couroutinstudy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.AlarmRequest

@Dao
interface AlarmDao {

    @Query("SELECT *FROM ALARM")
    fun getAll() : LiveData<List<Alarm>>

    @Insert
    suspend fun insert(alarm: Alarm) : Long

    @Query("UPDATE ALARM SET isRepeat = :isRepeat WHERE id =:alarmId")
    suspend fun updateIsRepeat(isRepeat : Boolean, alarmId : Int)

    @Query("UPDATE ALARM SET isOn = :onOff WHERE id =:alarmId")
    suspend fun updateOnOff(onOff : Boolean, alarmId : Int)

    @Query("SELECT MAX(id) FROM ALARM")
    suspend fun selectLastAlarmId() : Int?

    @Insert
    suspend fun insertRequestCode(requestCode: AlarmRequest)

    @Query("DELETE FROM ALARM")
    suspend fun deleteAll() : Void

    @Delete
    suspend fun delete(alarm : Alarm)
}