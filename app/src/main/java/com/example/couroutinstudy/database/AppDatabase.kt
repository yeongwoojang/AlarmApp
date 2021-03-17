package com.example.couroutinstudy.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.couroutinstudy.model.vo.Alarm

@Database(entities = [Alarm::class],version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun alarmDao() : AlarmDao
}