package com.example.couroutinstudy.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class ModifyViewModel(application: Application) : BaseViewModel(application){

    val updateDayOfWeekLD = MutableLiveData<Boolean>()
    val alarmLdForModyfyActi = MutableLiveData<Alarm>()

    fun updateDayOfWeek(dayOfWeek:  List<DayOfWeek>, alarmId : Int){
        viewModelScope.launch(IO) {
            db!!.alarmDao().updateDayOfWeek(dayOfWeek,alarmId)
            updateDayOfWeekLD.postValue(true)
        }
    }


}