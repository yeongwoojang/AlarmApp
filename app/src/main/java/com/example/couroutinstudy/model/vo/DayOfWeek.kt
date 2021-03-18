package com.example.couroutinstudy.model.vo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity
data class DayOfWeek(var dayOfWeek : String, var isCheck : Boolean){
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}