package com.example.couroutinstudy.model.vo

import androidx.room.Entity

@Entity
data class DayOfWeek(var dayOfWeek : String, var isCheck : Boolean)