package com.example.couroutinstudy.view.adapter

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.databinding.ItemAlarmBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlarmAdapter(private val mContext: Context, private val viewModel: BaseViewModel) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private val thisObj = this
    private var items: List<Alarm> = mutableListOf<Alarm>()
    private var isWholeUpdate = false
    private var updatePosition = 0

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
        if(isWholeUpdate) notifyDataSetChanged()
        else notifyItemChanged(updatePosition)
    }

    fun activeAlarm(position: Int) {
        isWholeUpdate = false
        updatePosition = position
        items[position].isOn = !items[position].isOn
        viewModel.updateOnOff(items[position])



        //Vibrator(진동) 권한 사용
        val vibrator: Vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate(
            VibrationEffect.createOneShot(
                50,
                10
            )
        )
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
fun setActive(toggleButton: ToggleButton, alarm: Alarm) {
    toggleButton.isChecked = alarm.isOn

}