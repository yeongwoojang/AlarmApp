package com.example.couroutinstudy.model.vo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class DayOfWeek(var dayOfWeek : String, var requestCode :Int, var isCheck : Boolean) : Serializable{
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}