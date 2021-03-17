package com.example.couroutinstudy.view.adapter

import android.content.Context
import android.content.res.Resources
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.ItemAlarmBinding
import com.example.couroutinstudy.model.vo.Alarm


class AlarmAdapter(private val mContext : Context) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private val thisObj = this
    private var items: List<Alarm> = mutableListOf<Alarm>()

    inner class AlarmViewHolder(private val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.adapter = thisObj // adapter 객체 바인딩
        }

        fun bind(alarm: Alarm, position: Int) {
            binding.alarm = alarm //alarm객체 바인딩
            binding.position = position //리스트 position 바인딩
        }
    }


    // RecyclerView 업데이트
    fun updateItems(items: List<Alarm>) {
        this.items = items
        notifyDataSetChanged()
    }

    fun activeALarm(position : Int){
        items[position].isRepeat = !items[position].isRepeat
        notifyItemChanged(position)
        
        //Vibrator(진동) 권한 사용
        val vibrator : Vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)  vibrator.vibrate(VibrationEffect.createOneShot(50, 10))
        else vibrator.vibrate(200) //토글버튼 클릭 시 진동 발생


    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemAlarmBinding.inflate(inflater, parent, false)
        return AlarmViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item, position)
    }
}

@BindingAdapter("isActive")
fun setActive(toggleButtn : ToggleButton, alarm : Alarm){
    toggleButtn.isChecked = alarm.isRepeat
}