package com.example.couroutinstudy.view.activity

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.ActivityModifyAlarmBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.util.receiver.AlarmReceiver
import com.example.couroutinstudy.view.adapter.DayOfWeekAdapterForActi
import com.example.couroutinstudy.viewmodel.ModifyViewModel
import kotlinx.android.synthetic.main.fragment_day_of_week.*
import java.util.*
import kotlin.collections.ArrayList

class ModifyAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyAlarmBinding
    private lateinit var viewModel: ModifyViewModel
    private var beforeUpdateAlarmDayList = mutableListOf<Int>()
    private lateinit var alarm : Alarm
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyAlarmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        viewModel = ViewModelProvider(this)[ModifyViewModel::class.java]//뷰모델 초기화

        val intent = intent
        alarm = intent.getSerializableExtra("alarm") as Alarm // 알람목록중 하나 클릭 했을 시 넘겨받은 "Alarm"객체
        val adapter = DayOfWeekAdapterForActi(this, alarm, viewModel) //어댑터 설정

        Log.d("이게모지", "onCreate: ${alarm}")
        for (i in alarm.dayOfWeek.indices) {
            if (!alarm.dayOfWeek[i].isCheck) {
                Log.d("이게모지", "onCreate: ${i}")
                beforeUpdateAlarmDayList.add(i)
            }
        }
        Log.d("이게모지", "onCreate: ${beforeUpdateAlarmDayList}")

        binding.dayOfWeekRv.let {//리사이클러뷰, 설정
            it.adapter = adapter
            it.layoutManager =
                LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) //리사이클러뷰를 가로로 사용
            val animator = it.itemAnimator //RecyclerView를 update했을 시 화면이 깜빡이는 현상을 제거하는 부분
            if (animator is SimpleItemAnimator) animator.supportsChangeAnimations = false
        }
        binding.dayOfWeekRv.adapter = adapter //리사이클러뷰에 어댑터 장착

        val timeStr = alarm.time //"Alarm" 객체에 등록되어있는 시간

        val arr = timeStr?.split(":")
        val hour = arr!![0].toInt()
        val minute = arr[1].toInt()
        val time = mutableMapOf<String, Int>()
        time.put("hourOfDay", hour)
        time.put("minute", minute)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.timePicker.hour = time.get("hourOfDay")!!
            binding.timePicker.minute = time.get("minute")!!
        } else {
            binding.timePicker.currentHour = time.get("hourOfDay")!!
            binding.timePicker.currentMinute = time.get("minute")!!
        }

        binding.btnCancel.setOnClickListener {// 취소버튼 클릭 이벤트
            finish()
            overridePendingTransition(R.anim.none, R.anim.down_animation)
        }

        binding.btnAlarmModify.setOnClickListener { //수정버튼 클릭 이벤트
            Log.d("HelloWorld", "수정버튼클릭")
            for (i in beforeUpdateAlarmDayList.indices) {
                //if문 : 알람이 울리는 요일을 수정해서 on으로 바꿨을 시
                //alarm : 요일을 수정하기 전 "Alarm"객체
                //modifiedAlarm : 요일 수정 후 "Alarm"객체
                val position = beforeUpdateAlarmDayList[i]
                if (alarm.dayOfWeek[position].isCheck){
                    //바꾼 요일만 새로 알람을 등록해준다.
                    Log.d("HelloWorld", "onCreate: $position")
                    val time = alarm.time
                    val arr = time?.split(":")
                    val hourOfDay = arr!!.get(0) //알람이 울릴 "시간"
                    val minute = arr!!.get(1) //알람이 울릴 "분"
                    val cal = Calendar.getInstance()
                    val dayOfWeekCode = position+2 //요일 코드
                    cal.set(Calendar.DAY_OF_WEEK,dayOfWeekCode)
                    cal.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
                    cal.set(Calendar.MINUTE, minute.toInt())
                    Log.d("calTime", "onCreate: ${cal.time}")
                    val alarmManager = this.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val alarmIntent = Intent(this, AlarmReceiver::class.java)
                    alarmIntent.action="sendNotification"
                    setPendingIntent(alarmManager, alarmIntent, cal, position)
                }
            }

            viewModel.updateDayOfWeek(alarm.dayOfWeek, alarm.id)
            finish()
            overridePendingTransition(R.anim.none, R.anim.down_animation)
        }

        viewModel.alarmLd.observe(this, Observer { modifiedAlarm ->
            Log.d("dsfgsdgsdfgsdfgdsfgdf", "sdfgsdfg: sdfsdsadf")
            alarm = modifiedAlarm
        })


        viewModel.updateDayOfWeekLD.observe(this, Observer { isUpdate ->
            if (isUpdate) {
                Log.d("safg", "sfgsfdg: 업데이트 완료")

            }
        })
    }
    private fun setPendingIntent( alarmManager: AlarmManager?,alarmIntent: Intent,cal: Calendar,index: Int){
        var position = index

        val bundle = Bundle()
        bundle.putSerializable("alarmData", alarm)
        bundle.putSerializable("alarmDate", cal)
        alarmIntent.putExtra("bundle", bundle)
        Log.d("ALAMR", "setPendingIntent: ${alarm}")
        if(position==-1){
            val calendar = Calendar.getInstance()
            val dayOfWeekCode = calendar.get(Calendar.DAY_OF_WEEK) //오늘의 요일을 구한다.
            position = if (dayOfWeekCode != 1) dayOfWeekCode - 2 else 6 //요일에 맞는 인덱스 설정
        }

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            this
            , alarm.dayOfWeek[position].requestCode
            , alarmIntent
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