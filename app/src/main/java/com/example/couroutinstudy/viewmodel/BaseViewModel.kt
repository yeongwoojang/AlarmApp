package com.example.couroutinstudy.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class BaseViewModel(application: Application) : AndroidViewModel(application) {


    val slideLd  = MutableLiveData<Boolean>()
    val fragIdLd = MutableLiveData<Int>()
    val hourOfDayLd = MutableLiveData<Int>()
    val timeLd = MutableLiveData<Map<String,Int>>()



    init {  //초기화 블록
        slideLd.value = false
    }


    fun openSlide() {// open slide
        slideLd.value = true
    }

    fun closeSlide() { //close slide
        slideLd.value = false
    }

    fun changeFragment(id : Int){
        fragIdLd.value = id
    }

    fun updateTime(hourOfDay : Int, minute : Int){
        val map = mutableMapOf<String,Int>()
        map.put("hourOfDay",hourOfDay)
        map.put("minute",minute)

        timeLd.value = map
    }

}