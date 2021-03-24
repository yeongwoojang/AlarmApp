package com.example.couroutinstudy.model.vo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class AlarmRequest(val requestCode : String) {
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}