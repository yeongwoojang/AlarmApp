package com.example.couroutinstudy.model.vo

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
class Alarm() :Serializable{
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
    var amPm: String? = null
    var time: String? = null
    var isOn : Boolean  = false
    var dayOfWeek : List<DayOfWeek> = mutableListOf(
        DayOfWeek("월요일마다", -1,false),
        DayOfWeek("화요일마다", -1,false),
        DayOfWeek("수요일마다", -1,false),
        DayOfWeek("목요일마다", -1,false),
        DayOfWeek("금요일마다", -1,false),
        DayOfWeek("토요일마다", -1,false),
        DayOfWeek("일요일마다", -1,false)
    )
    constructor(amPm : String,time : String, onOff : Boolean, isRepeat : Boolean) : this(){
        this.amPm = amPm
        this.time = time
        this.isOn = onOff
    }

    override fun toString(): String {
        return "Alarm(id=$id, amPm=$amPm, time=$time, isOn=$isOn,dayOfWeek=$dayOfWeek)"
    }


}

// apPm : 오전 오후 여부
// time : 알람이 울릴 시간
// isRepeat : 설정한 알림이 한번만 울릴 것인지 아니면 지정한 시간에 매일 울릴 것인지 결정
// dayOfWeek : 알람이 몇요일마다 울릴 것인지에 대한 정보를 담고있는 리스트