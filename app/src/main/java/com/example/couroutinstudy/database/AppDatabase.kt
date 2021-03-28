package com.example.couroutinstudy.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.AlarmRequest
import com.example.couroutinstudy.model.vo.DayOfWeek

@Database(entities = [Alarm::class, DayOfWeek::class, AlarmRequest::class],version = 1)
@TypeConverters(Converter::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun alarmDao() : AlarmDao

    //companion object는 static이 아니라 객체이다.
    //보기엔 static 처럼 사용할 수 있지만 사실은 class.companion.xxx 로 동작한다.
    companion object{
        private var instance : AppDatabase? = null

        @Synchronized
        fun getInstance(context : Context) : AppDatabase?{
            if(instance == null){
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database-contacts"
                ).allowMainThreadQueries()
                    .build()
            }
            return instance
        }

    }

}