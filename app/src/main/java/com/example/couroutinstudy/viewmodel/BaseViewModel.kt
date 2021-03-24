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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BaseViewModel(application: Application) : AndroidViewModel(application) {


    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java, "AlarmDatabase"
    ).fallbackToDestructiveMigration()
        .build()

    private val workManageer: WorkManager = WorkManager.getInstance(application.applicationContext)

    val slideLd = MutableLiveData<Boolean?>()
    val fragIdLd = MutableLiveData<Int>()
    val timeLd = MutableLiveData<Map<String, Int>>()
    val alarmLd = MutableLiveData<Alarm>()
    val lastAlarmIdLd: MutableLiveData<Int>? = MutableLiveData<Int>()
    var alarms: LiveData<List<Alarm>>
    val requestCodeLd = MutableLiveData<List<String>>()
    val codeLd = MutableLiveData<Int>()

    init {//초기화 블록
        slideLd.value = false
        alarms = getAll()
    }

    fun getAll(): LiveData<List<Alarm>> {
        return db.alarmDao().getAll()
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            db.alarmDao().deleteAll()
        }
    }

    fun insertAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            db.alarmDao().insert(alarm)
        }
        Log.d(AlarmMainFrag.TAG, "onViewCreated: selectLastAlarmId()실행")
//        selectLastAlarmId()
    }

    fun updateIsRepeat(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            db.alarmDao().updateIsRepeat(alarm.isRepeat, alarm.id)
        }
    }

    fun selectLastAlarmId() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("Adsf", "selectLastAlarmId실행")
            val lastAlarmId: Int? = db.alarmDao().selectLastAlarmId()
            if (lastAlarmId != null) {
                lastAlarmIdLd?.postValue(lastAlarmId)
            } else {
                lastAlarmIdLd?.postValue(1)
            }
        }
    }

    fun updateOnOff(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            db.alarmDao().updateOnOff(alarm.isOn, alarm.id)
        }
    }

    fun selectRequestCode2(requestCode: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val code = db.alarmDao().selectRequestCode2(requestCode)
            codeLd.postValue(code)
        }
    }

    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            db.alarmDao().delete(alarm)
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