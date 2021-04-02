package com.example.couroutinstudy.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.AlarmRequest
import com.example.couroutinstudy.model.vo.DayOfWeek

@Dao
interface AlarmDao {

    @Query("SELECT *FROM ALARM")
    fun getAll() : LiveData<List<Alarm>>

    @Insert
    suspend fun insert(alarm: Alarm) : Long

    @Query("UPDATE ALARM SET isOn = :onOff WHERE id =:alarmId")
    suspend fun updateOnOff(onOff : Boolean, alarmId : Int)

    @Query("UPDATE ALARM SET dayOfWeek= :dayOfWeek WHERE id=:alarmId")
    suspend fun updateDayOfWeek(dayOfWeek: List<DayOfWeek>, alarmId :Int)

    @Insert
    suspend fun insertRequestCode(requestCode: AlarmRequest)

    @Query("DELETE FROM ALARM")
    suspend fun deleteAll() : Void

    @Delete
    suspend fun delete(alarm : Alarm)
}