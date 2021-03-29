package com.example.couroutinstudy.view.adapter

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.databinding.ItemAlarmBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.util.receiver.AlarmReceiver
import com.example.couroutinstudy.view.fragment.AlarmMainFrag
import com.example.couroutinstudy.viewmodel.BaseViewModel
import java.util.*


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
        if (isWholeUpdate) notifyDataSetChanged() //리사이클러뷰 전체사항에 대한 변경이라면 notifyDataSetChanged()
        else notifyItemChanged(updatePosition) //아니라면 변화가 있는 포지션에 대해서만 notifyItemChanged(position)
    }


    fun activeAlarm(position: Int) {
        isWholeUpdate = false //토큰버튼을 클릭하면 리사이클러뷰에서 하나의 아이템만 변화하므로 "isWholeUpdate" = false
        updatePosition = position //업데이트 된 포지션값 저장
        val isOn = !items[position].isOn
        items[position].isOn = isOn //리사이클러뷰 해당 포지션번째 아이탬의 isOn 속성을 on Off 하는 부분
        viewModel.updateOnOff(items[position]) //클릭한 아이템의 "isOn"변수를 변경하면 "Database"에서도 변경 적용

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

        if (!isOn) {
            Log.d("asdfdasf", "sfgsd : isOn -> false ")
            for (i in 0..6) { //선택한 Alarm 아이템의 예약된 모든 알람을 Cancel
                if (items[position].dayOfWeek[i].requestCode != -1) {
                    val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(mContext, AlarmReceiver::class.java)
                    intent.action = "sendNotification"
                    val pendingIntent =
                        PendingIntent.getBroadcast(
                            mContext
                            , items[position].dayOfWeek[i].requestCode, intent
                            , PendingIntent.FLAG_CANCEL_CURRENT
                        )
                    alarmManager.cancel(pendingIntent)
                }
            }
        } else {
            Log.d("asdfdasf", "sfgsd : isOn -> true ")
            val cal = Calendar.getInstance()
            val time = items[position].time //Alarm 객체에 담긴 시간을 calendar 객체에 지정
            val arr = time?.split(":")
            val hourOfDay = arr!!.get(0) //알람이 울릴 "시간"
            val minute = arr!!.get(1) //알람이 울릴 "분"

            for (i in 0..6) { //선택한 Alarm 아이템의 예약된 모든 알람을 Cancel
                if (items[position].dayOfWeek[i].requestCode != -1) {
                    cal.set(Calendar.DAY_OF_WEEK, i + 2) // Calendar 객체에 Alarm 객체에 체크된 요일을 Setting
                    // Calendar 객체에 알람이 울릴 시간과 분을 지정
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
                    cal.set(Calendar.MINUTE, minute.toInt())
//                    cal.set(Calendar.SECOND,0)
//                    cal.set(Calendar.MILLISECOND,0)
                    Log.d(AlarmMainFrag.TAG, "alarm adapter : ${cal.time} ")

                    setPendingIntent(items[position], cal, i)
                }
            }
        }


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

    private fun setPendingIntent(
        alarm: Alarm,
        cal: Calendar,
        index: Int
    ) {
        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(mContext, AlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable("alarmData", alarm)
        bundle.putSerializable("alarmDate", cal)
        intent.putExtra("bundle", bundle)
        intent.action = "sendNotification"
        Log.d("asdfdasf", "sfgsd : ${cal.time} ")
        val pendingIntent =
            PendingIntent.getBroadcast(
                mContext.applicationContext
                , alarm.dayOfWeek[index].requestCode, intent
                , PendingIntent.FLAG_CANCEL_CURRENT
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pendingIntent
            )
        }
    }
}

@BindingAdapter("isActive")
fun setActive(toggleButton: ToggleButton, alarm: Alarm) {
    toggleButton.isChecked = alarm.isOn

}

@BindingAdapter("selectedDay")
fun setSelectedColor(textView: TextView, alarm: Alarm) {
    val ssb = SpannableStringBuilder(textView.text)
    val textLength = 13
    for (i in 0..6) {
        if (alarm.dayOfWeek[i].isCheck) {
            Log.d(
                "asd",
                "setSelectedColor: ${alarm.dayOfWeek[i].dayOfWeek}${alarm.dayOfWeek[i].isCheck}"
            )
            ssb.setSpan(
                ForegroundColorSpan(Color.parseColor("#FB8C00")),
                i,
                i + 1,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
    }

    textView.setText(ssb)
}