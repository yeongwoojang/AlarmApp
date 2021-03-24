package com.example.couroutinstudy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.AlarmRequest
import com.example.couroutinstudy.model.vo.DayOfWeek

@Database(entities = [Alarm::class, DayOfWeek::class, AlarmRequest::class],version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun alarmDao() : AlarmDao
}