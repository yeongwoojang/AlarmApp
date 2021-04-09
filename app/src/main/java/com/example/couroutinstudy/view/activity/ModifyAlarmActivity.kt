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
import com.example.couroutinstudy.view.fragment.AlarmMainFrag
import com.example.couroutinstudy.viewmodel.ModifyViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class ModifyAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityModifyAlarmBinding
    private lateinit var viewModel: ModifyViewModel
    private var offAlarmDayList = mutableListOf<Int>() //알람이 off 되어있는 요일의 인덱스를 담을 "List"
    private var onAlarmDayList = mutableListOf<Int>()
    private var prevAlarmTime: String? = ""
    private var newRegisterCount: Int = 0
    private lateinit var alarm: Alarm
    private lateinit var mContext: Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyAlarmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        mContext = this
        viewModel = ViewModelProvider(this)[ModifyViewModel::class.java]//뷰모델 초기화

        val intent = intent
        alarm = intent.getSerializableExtra("alarm") as Alarm // 알람목록중 하나 클릭 했을 시 넘겨받은 "Alarm"객체
        val adapter = DayOfWeekAdapterForActi(this, alarm, viewModel) //어댑터 설정
        prevAlarmTime = alarm.time

        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            Log.d(AlarmMainFrag.TAG, "onViewCreated: 타임피커 읽음")
            val amPm = if (hourOfDay < 12) "오전" else "오후" //선택한 시간이 오전인지 오후인지 식별
            val hour: Any = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
            val min: Any = if (minute < 10) "0${minute}" else "${minute}"
            val time = "${hour}:${min}"
            alarm.amPm = amPm
            alarm.time = time
        }
        for (i in alarm.dayOfWeek.indices) {
            if (!alarm.dayOfWeek[i].isCheck) {
                offAlarmDayList.add(i) //알람이 off되어있는 요일의 인덱스를 추가
            } else {
                onAlarmDayList.add(i)
            }
        }

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

        binding.btnAlarmDelete.setOnClickListener {
            viewModel.deleteAlarm(alarm)
            for (i in 0..6) {
                if (alarm.dayOfWeek[i].requestCode != -1) {
                    val alarmManager =
                        mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val intent = Intent(mContext, AlarmReceiver::class.java)
                    intent.action = "sendNotification"
                    val pendingIntent =
                        PendingIntent.getBroadcast(
                            mContext
                            , alarm.dayOfWeek[i].requestCode, intent
                            , PendingIntent.FLAG_CANCEL_CURRENT
                        )
                    alarmManager.cancel(pendingIntent)
                }
            }
            finish()
            overridePendingTransition(R.anim.none, R.anim.down_animation)
        }
        //맨처음에는 알람이 등록되어있는 요일이었다가 알람이 취소되었다면 처리해야되는데
        //어떻게 처리해야 할까?

        binding.btnAlarmModify.setOnClickListener { //수정버튼 클릭 이벤트
                val time = alarm.time
                val arr = time?.split(":")
                val hourOfDay = arr!!.get(0) //알람이 울릴 "시간"
                val minute = arr!!.get(1) //알람이 울릴 "분"
                val cal = Calendar.getInstance()
                var requestCodeCnt = 0
                //알람을 수정할 수 있다는 것 자체가 무조건 하나의 요일에는 "requestCode"가 담겨있다는 것이다.
                for (i in 0..6) {
                    if (alarm.dayOfWeek[i].isCheck) { //요일이 체크 되어있는 상태라면
                        newRegisterCount++
                        cal.set(Calendar.DAY_OF_WEEK,i + 2) // Calendar 객체에 Alarm 객체에 체크된 요일을 Setting
                        // Calendar 객체에 알람이 울릴 시간과 분을 지정
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
                        cal.set(Calendar.MINUTE, minute.toInt())
                        if (alarm.dayOfWeek[i].requestCode == -1) {
                            val pId = (Math.random() * 100000000).toInt()
                            alarm.dayOfWeek[i].requestCode = pId

                        }
                        val alarmManager = mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val alarmIntent = Intent(mContext, AlarmReceiver::class.java)
                        alarmIntent.action = "sendNotification"
                        setPendingIntent(alarmManager, alarmIntent, cal, i)
                    } else { //요일이 체크 되어있지 않은 상태라면
                        //알람을 삭제해야한다.
                        val alarmManager =
                            mContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val intent = Intent(mContext, AlarmReceiver::class.java)
                        intent.action = "sendNotification"

                        val pendingIntent = PendingIntent.getBroadcast(
                            mContext
                            , alarm.dayOfWeek[i].requestCode //해당 요일에 등록되어있던 알람요청 코드
                            , intent
                            , PendingIntent.FLAG_CANCEL_CURRENT
                        )
                        alarmManager.cancel(pendingIntent)
                        alarm.dayOfWeek[i].requestCode = -1 //해당 요일에 알람 requestCode 삭제
                    }
                }
            if (newRegisterCount > 0) {
                Log.d("newRegister", "newRegister$newRegisterCount")
                alarm.isOn = true
            }else{
                alarm.isOn = false
            }

            viewModel.updateAlarm(alarm.amPm, alarm.time,alarm.isOn,alarm.dayOfWeek,alarm.id)
//            viewModel.updateDayOfWeek(alarm.dayOfWeek, alarm.id)
            finish()
            overridePendingTransition(R.anim.none, R.anim.down_animation)
        }

        //알람이 울리는 요일을 수정할 때마다 Observe하는 리스너
        viewModel.alarmLd.observe(this, Observer { modifiedAlarm ->
            //modifiedAlarm : 요일 수정 후 "Alarm"객체
            alarm = modifiedAlarm
        })


        viewModel.updateDayOfWeekLD.observe(this, Observer { isUpdate ->
            if (isUpdate) {
                Log.d("safg", "sfgsfdg: 업데이트 완료")

            }
        })
    }

    private fun setPendingIntent(
        alarmManager: AlarmManager?,
        alarmIntent: Intent,
        cal: Calendar,
        index: Int
    ) {
        var position = index

        val bundle = Bundle()
        bundle.putSerializable("alarmData", alarm)
        bundle.putSerializable("alarmDate", cal)
        alarmIntent.putExtra("bundle", bundle)
        Log.d("ALAMR", "setPendingIntent: ${alarm}")
        if (position == -1) {
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