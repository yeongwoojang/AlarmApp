package com.example.couroutinstudy.database

import androidx.room.TypeConverter
import com.example.couroutinstudy.model.vo.DayOfWeek
import com.google.gson.Gson

class Converter {
    @TypeConverter
    fun listToJson(value : List<DayOfWeek>) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value : String) = Gson().fromJson(value, Array<DayOfWeek>::class.java).toList()
}