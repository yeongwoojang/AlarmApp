package com.example.couroutinstudy.view.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.database.AppDatabase
import com.example.couroutinstudy.databinding.ItemDayOfWeekForActiBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import com.example.couroutinstudy.util.receiver.AlarmReceiver
import com.example.couroutinstudy.viewmodel.ModifyViewModel
import java.util.*

class DayOfWeekAdapterForActi(private val mContext: Context, private var alarm: Alarm,private val viewModel : ModifyViewModel) :
    RecyclerView.Adapter<DayOfWeekAdapterForActi.DayViewHolder>() {

    private val thisObj = this
    private val db: AppDatabase? = AppDatabase.getInstance(mContext)
    private val dayList: List<String> = mutableListOf("월", "화", "수", "목", "금", "토", "일")

    inner class DayViewHolder(private val binding: ItemDayOfWeekForActiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.adapter = thisObj
        }

        fun bind(dayOfWeek: String, dayAlarm: DayOfWeek, position: Int) {
            binding.dayAlarm = dayAlarm
            binding.dayOfWeek = dayOfWeek
            binding.position = position
        }

    }

    fun updateItem(alarm: Alarm){
        this.alarm = alarm
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDayOfWeekForActiBinding.inflate(inflater, parent, false)
        return DayViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dayList.size
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {

        val item = dayList[position]
        val dayAlarm = alarm.dayOfWeek[position]
        holder.bind(item, dayAlarm, position)
    }

    fun dayOfWeekClick(position: Int) {
        val dayAlarm = alarm.dayOfWeek
        dayAlarm[position].isCheck = !dayAlarm[position].isCheck // 해당요일의 알람을 울릴 것인지 안울릴 것인지 on 또는 off

        if(dayAlarm[position].isCheck){ //요일을 on 해주면
            val pId = (Math.random() * 100000000).toInt()
            dayAlarm[position].requestCode = pId // 해당요일에 알람 requestCode 생성

            Log.d("asdf", "dayOfWeekClick: ${dayAlarm[position]}")
        }else{ //요일을 off해주면
//            dayAlarm[position].requestCode = -1 //해당 요일에 알람 requestCode 삭제
        }
        notifyItemChanged(position)
        alarm.dayOfWeek = dayAlarm
        viewModel.setAlarm(alarm)

//        viewModel.updateDayOfWeek(dayAlarm,alarm.id)
    }
}


@BindingAdapter("setDayColor")
fun setDayColor(textView: TextView, dayAlarm: DayOfWeek) {
    if (dayAlarm.isCheck) {
        textView.setTextColor(Color.parseColor("#FFA726"))
    } else {
        textView.setTextColor(Color.parseColor("#FFFFFF"))
    }

}