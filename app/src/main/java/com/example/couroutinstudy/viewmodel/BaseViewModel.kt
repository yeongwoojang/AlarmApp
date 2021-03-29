package com.example.couroutinstudy.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import androidx.work.WorkManager
import com.example.couroutinstudy.database.AppDatabase
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.AlarmRequest
import com.example.couroutinstudy.view.fragment.AlarmMainFrag
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

open class BaseViewModel(application: Application) : AndroidViewModel(application) {


    var db: AppDatabase? = null

    private val workManageer: WorkManager = WorkManager.getInstance(application.applicationContext)

    val slideLd = MutableLiveData<Boolean?>()
    val fragIdLd = MutableLiveData<Int>()
    val timeLd = MutableLiveData<Map<String, Int>>()
    val alarmLd = MutableLiveData<Alarm>()
    var alarms: LiveData<List<Alarm>>
    val rowNumLd = MutableLiveData<Long>()

    init {//초기화 블록
        db = AppDatabase.getInstance(application) //AppDatabase 싱글톤 객체를 가져온다.
        Log.d("BaseViewModel", "ViewModel InIt: ")
        alarms = getAll()
    }

    fun getAll(): LiveData<List<Alarm>> {
        return db!!.alarmDao().getAll()
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            db!!.alarmDao().deleteAll()
        }
    }

    fun insertAlarm(alarm: Alarm) {
        Log.d("insertAlarm", "insertAlarm: ${alarm.id}")
        viewModelScope.launch(Dispatchers.IO) {
            val rowNum = db!!.alarmDao().insert(alarm)
            rowNumLd.postValue(rowNum)
        }
    }
        fun updateIsRepeat(alarm: Alarm) {
            viewModelScope.launch(Dispatchers.IO) {
                db!!.alarmDao().updateIsRepeat(alarm.isRepeat, alarm.id)
            }
        }


        fun updateOnOff(alarm: Alarm) {
            viewModelScope.launch(Dispatchers.IO) {
                db!!.alarmDao().updateOnOff(alarm.isOn, alarm.id)
            }
        }


        fun deleteAlarm(alarm: Alarm) {
            viewModelScope.launch(Dispatchers.IO) {
                db!!.alarmDao().delete(alarm)
            }
        }

        fun openSlide() {// open slide
            slideLd.value = true
        }

        fun closeSlide() { //close slide
            slideLd.value = false
        }

        fun changeFragment(id: Int) { //프래그먼트 전환 메소드
            fragIdLd.value = id
        }

        fun updateTime(hourOfDay: Int, minute: Int) {
            val map = mutableMapOf<String, Int>()
            map.put("hourOfDay", hourOfDay)
            map.put("minute", minute)
            timeLd.value = map
        }

        fun setAlarm(alarm: Alarm) {
            alarmLd.value = alarm
        }
}