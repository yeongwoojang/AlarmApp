package com.example.couroutinstudy.view.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ToggleButton
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.couroutinstudy.databinding.FragmentAlarmMainBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.AlarmRequest
import com.example.couroutinstudy.util.receiver.AlarmReceiver
import com.example.couroutinstudy.view.activity.MainActivity
import com.example.couroutinstudy.viewmodel.BaseViewModel
import java.util.*

class AlarmMainFrag : Fragment() {

    companion object {
        val TAG = AlarmMainFrag.javaClass.simpleName
        const val DAY_OF_WEEK_FRAGMENT: Int = 1
    }

    private var _binding: FragmentAlarmMainBinding? = null //binding 객체
    private val binding get() = _binding!! //binding객체 getter
    private lateinit var viewModel: BaseViewModel //뷰모델 객체
    private lateinit var mActivity: MainActivity
    private lateinit var alarm: Alarm//알람정보를 저장할 객체


    private var alarmManager: AlarmManager? = null
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mActivity = activity as MainActivity
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAlarmMainBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        viewModel = ViewModelProvider(activity as FragmentActivity)[BaseViewModel::class.java] 프래그먼트에서 뷰모델 생성 방법 1
//        viewModel = ViewModelProvider(activity as ViewModelStoreOwner)[BaseViewModel::class.java] 프래그먼트에서 뷰모델 생성 방법 2
        viewModel =
            ViewModelProvider(requireActivity())[BaseViewModel::class.java] //프래그먼트에서 뷰모델 생성 방법 3

        //프래그먼트에서 위의 방식과 같이 뷰모델을 생성하면 액티비티에서 생성한 뷰모델을 공유한다.
        //requireActivity는 getActivity가 null일 경우 IllegalStatementException을 던진다.
        this.alarm = Alarm()
        binding.alarm = alarm //alarm 데이터 바인딩
        
        //취소버튼 클릭 이벤트
        binding.btnCancel.setOnClickListener {
            this.alarm =
                Alarm() // slidingView가 닫히고 다시 열릴 떄 onViewCreated를 타지 않게 했기 때문에 alarm객체를 다시 초기화한다.
            viewModel.setAlarm(alarm)
            viewModel.updateTime(0, 0) // 알람객체의 hourOfDay와 minute를 0으로 초기화(Default = null)
            viewModel.closeSlide() //취소 버튼 클릭 시 슬라이드 닫음
        }

        //알람 저장 버튼 클릭 이벤트
        binding.btnAlarmSave.setOnClickListener {
            //알림 저장 버튼 클릭 시 실행되어야 할 코드 작성
            val pId = (Math.random()*100000000).toInt()
            checkAlarmData() //alarm null Check
            alarm.isOn = true
            alarm.requestCode = pId
            registerAlarm(alarm)
            viewModel.insertAlarm(alarm)
            this.alarm =
                Alarm() // slidingView가 닫히고 다시 열릴 떄 onViewCreated를 타지 않게 했기 때문에 alarm객체를 다시 초기화한다.
            viewModel.setAlarm(alarm)
            viewModel.updateTime(0, 0)
            viewModel.closeSlide()
        }

        binding.menuRepeat.setOnClickListener {//반복 버튼 클릭 이벤트
            val bundle = Bundle()
            checkAlarmData() //alarm null Check
            bundle.putSerializable("alarmInfo", alarm)
            mActivity.changeBundle(bundle)
            viewModel.changeFragment(DAY_OF_WEEK_FRAGMENT) //요일 선택 프래그먼트로 교체
        }

        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            Log.d(TAG, "onViewCreated: 타임피커 읽음")
            val amPm = if (hourOfDay < 12) "오전" else "오후" //선택한 시간이 오전인지 오후인지 식별
            val hour: Any = if (hourOfDay < 10) "0${hourOfDay}" else "${hourOfDay}"
            val min: Any = if (minute < 10) "0${minute}" else "${minute}"
            val time = "${hour}:${min}"
            alarm.amPm = amPm
            alarm.time = time
            viewModel.updateTime(hourOfDay, minute)
        }

        binding.btnActiveRepeatAlarm.setOnClickListener {
            //다시알림 메뉴를 활성화 했을 때 실행되어야 할 코드 작성
            alarm.isRepeat = !alarm.isRepeat
            checkAlarmData() //alarm null Check
            viewModel.setAlarm(alarm)

        }
        //프래그먼트가 내려갔다가 다시 올라와도 시간을 유지시키기 위한 timeLiveData observe
        viewModel.timeLd.observe(requireActivity(), Observer { time ->
            //API23버전 이전과 이후로 방법이 다르기 때문에 분기처리
            if (time.get("hourOfDay") != 0 && time.get("minute") != 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.timePicker.hour = time.get("hourOfDay")!!
                    binding.timePicker.minute = time.get("minute")!!
                } else {
                    binding.timePicker.currentHour = time.get("hourOfDay")!!
                    binding.timePicker.currentMinute = time.get("minute")!!
                }
            } else {
                val cal = Calendar.getInstance()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.timePicker.hour = cal.get(Calendar.HOUR_OF_DAY)
                    binding.timePicker.minute = cal.get(Calendar.MINUTE)
                } else {
                    binding.timePicker.currentHour = cal.get(Calendar.HOUR_OF_DAY)
                    binding.timePicker.currentMinute = cal.get(Calendar.MINUTE)
                }
            }
        })

        viewModel.alarmLd.observe(requireActivity(), Observer { alarm ->
            this.alarm = alarm
            _binding?.alarm = alarm //프래그먼트의 생명주기가 끝나면 _binding을 null로 해주었기 때문에
            // binding getter로 접근이 불가하기 때문에 변수로 직접 접근.
        })

        viewModel.lastAlarmIdLd?.observe(requireActivity(), Observer { id->


        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //프래그먼트의 생명주기가 끝날 때 binding도 같이 삭제
        _binding = null
    }

    //TimePicker를 건드리지 않았을 때 alarm객체의
    //amPm과 time필드가 null이기 때문에 이 두 필드의
    //null체크를함과 동시에 초기화 해주는 메소드
    private fun checkAlarmData() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarm.amPm = if (binding.timePicker.hour < 12) "오전" else "오후"
                val hour: Any =
                    if (binding.timePicker.hour < 10) "0${binding.timePicker.hour}" else "${binding.timePicker.hour}"
                val minute: Any =
                    if (binding.timePicker.minute < 10) "0${binding.timePicker.minute}" else "${binding.timePicker.minute}"
                alarm.time = "${hour}:${minute}"
            } else {
                alarm.amPm = if (binding.timePicker.currentHour < 12) "오전" else "오후"
                val hour: Any =
                    if (binding.timePicker.currentHour < 10) "0${binding.timePicker.currentHour}" else "${binding.timePicker.currentHour}"
                val minute: Any =
                    if (binding.timePicker.currentMinute < 10) "0${binding.timePicker.currentMinute}" else "${binding.timePicker.currentMinute}"
                alarm.time =
                    "${hour}:${minute}"
            }
    }

    private fun registerAlarm(alarm: Alarm) {
        val cal = Calendar.getInstance() // 알람을 저장할 calendar
        val time = alarm.time //Alarm 객체에 담긴 시간을 calendar 객체에 지정

        val arr = time?.split(":")
        val hourOfDay = arr!!.get(0) //알람이 울릴 "시간"

        val minute = arr!!.get(1) //알람이 울릴 "분"

//        val date = Date() //현재 시간정보를 담고있는 Date 객체
//        cal.time = date //Calendar 객체에 현재시간을 할당
        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity?.applicationContext, AlarmReceiver::class.java)
        alarmIntent.action = "sendNotification"

        var dayOfWeekCnt = 0
        Log.d("뭐야진짜", "${alarm.dayOfWeek}")
        for (i in 0..6) { //인덱스 0~6까지 조사
            //체크된 요일마다 알람을 예약하기 위한 조건문
            if (alarm.dayOfWeek[i].isCheck) { //체크된 요일이 있다면
                Log.d(TAG, "registerAlarm: 반복알람등록")
                dayOfWeekCnt++ //체크된 요일이 몇개인지 조사
                cal.set(Calendar.DAY_OF_WEEK, i + 2) // Calendar 객체에 Alarm 객체에 체크된 요일을 Setting
                // Calendar 객체에 알람이 울릴 시간과 분을 지정
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
                cal.set(Calendar.MINUTE, minute.toInt())
                setPendingIntent(alarmManager,alarmIntent,cal) //PendingIntent 설정
            }
        }
        if(dayOfWeekCnt==0){ //체크된 요일이 하나도 없다면 일회성 알람 생성
            // Calendar 객체에 알람이 울릴 시간과 분을 지정
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
            cal.set(Calendar.MINUTE, minute.toInt())

            setPendingIntent(alarmManager,alarmIntent,cal) //PendingIntent 설정
        }
    }

    //intent에 담을 값과 penndingIntent 세팅
    private fun setPendingIntent(alarmManager: AlarmManager?,alarmIntent : Intent, cal :Calendar){
//        val pId = (Math.random()*100000000).toInt()
        val bundle = Bundle()
        bundle.putSerializable("alarmData", alarm)
        bundle.putSerializable("alarmDate", cal)
        alarmIntent.putExtra("bundle", bundle)
//        alarm.requestCode = pId
//        viewModel.insertRequestCode(AlarmRequest(alarm.id.toString()+pId))
        Log.d(TAG, "리퀘스트 아이디1: ${alarm.requestCode}")
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            activity?.applicationContext
            , alarm.requestCode
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

@BindingAdapter("isRepeat")
fun setRepeat(toggleButton: ToggleButton, alarm: Alarm) {
    toggleButton.isChecked = alarm.isRepeat
}
