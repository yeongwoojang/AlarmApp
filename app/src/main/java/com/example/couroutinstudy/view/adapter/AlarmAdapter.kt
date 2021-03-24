package com.example.couroutinstudy.view.adapter

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
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
import com.example.couroutinstudy.util.receiver.AlarmReceiver
import com.example.couroutinstudy.viewmodel.BaseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class AlarmAdapter(private val mContext: Context, private val viewModel: BaseViewModel) :
    RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    private val thisObj = this
    private var items: List<Alarm> = mutableListOf<Alarm>()
    private var isWholeUpdate = false //리사이클러뷰의 전체아이템의 변경인지 아닌지 확인하기 위한 변수
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
        if(isWholeUpdate) notifyDataSetChanged() //리사이클러뷰 전체사항에 대한 변경이라면 notifyDataSetChanged()
        else notifyItemChanged(updatePosition) //아니라면 변화가 있는 포지션에 대해서만 notifyItemChanged(position)
    }


    fun activeAlarm(position: Int) {
        isWholeUpdate = false //토큰버튼을 클릭하면 리사이클러뷰에서 하나의 아이템만 변화하므로 "isWholeUpdate" = false
        updatePosition = position //업데이트 된 포지션값 저장
        items[position].isOn = !items[position].isOn //리사이클러뷰 해당 포지션번째 아이탬의 isOn 속성을 on Off 하는 부분
        viewModel.updateOnOff(items[position]) //클릭한 아이템의 "isOn"변수를 변경하면 "Database"에서도 변경 적용
        Log.d("Asdsad", "리퀘스트 아이디2: ${items[position].requestCode}")
        if(!items[position].isOn){
            viewModel.selectRequestCode2(items[position].requestCode)
        }


        //Vibrator(진동) 권한 사용
        val vibrator: Vibrator = mContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) vibrator.vibrate( //Oreo버전 이상일 때만 실행.
            VibrationEffect.createOneShot(
                50,
                10
            )
        )
        //Oreo버전 이하일 때만 실행
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