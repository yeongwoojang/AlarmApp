package com.example.couroutinstudy.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ModifyViewModel(application: Application) : BaseViewModel(application){

    val updateDayOfWeekLD = MutableLiveData<Boolean>()
    val dayAlarmLd = MutableLiveData<Alarm>()
    val dayOfWeekListLd = MutableLiveData<List<Int>>()
    val updateAlarmLD = MutableLiveData<Boolean>()

    fun updateDayOfWeek(dayOfWeek:  List<DayOfWeek>, alarmId : Int){
        viewModelScope.launch(IO) {
            db!!.alarmDao().updateDayOfWeek(dayOfWeek,alarmId)
            updateDayOfWeekLD.postValue(true)
        }
    }

    fun modifyAlarm(alarm : Alarm){
        dayAlarmLd.value = alarm
    }

    fun updateAlarm(amPm : String?, time : String?, onOff : Boolean, dayOfWeek: List<DayOfWeek>, alarmId :Int){
        viewModelScope.launch(IO) {
            db!!.alarmDao().updateAlarm(amPm, time, onOff,dayOfWeek, alarmId)
            updateAlarmLD.postValue(true)
        }
    }
    fun updateDayOfList(list : List<Int>){
        dayOfWeekListLd.value = list
    }
    fun deleteAlarm(alarm: Alarm) {
        viewModelScope.launch(Dispatchers.IO) {
            db!!.alarmDao().delete(alarm)
        }
    }

}