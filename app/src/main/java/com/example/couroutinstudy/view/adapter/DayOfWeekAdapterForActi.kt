package com.example.couroutinstudy.view.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.R
import com.example.couroutinstudy.database.AppDatabase
import com.example.couroutinstudy.databinding.ItemDayOfWeekBinding
import com.example.couroutinstudy.databinding.ItemDayOfWeekForActiBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import com.example.couroutinstudy.viewmodel.ModifyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

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
        dayAlarm[position].isCheck = !dayAlarm[position].isCheck
        viewModel.setAlarm(alarm)
        viewModel.updateDayOfWeek(dayAlarm,alarm.id)
        notifyItemChanged(position)
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