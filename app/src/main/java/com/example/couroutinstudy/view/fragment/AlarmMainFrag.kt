package com.example.couroutinstudy.view.fragment

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.couroutinstudy.databinding.FragmentAlarmMainBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import com.example.couroutinstudy.model.vo.Test
import com.example.couroutinstudy.util.receiver.AlarmReceiver
import com.example.couroutinstudy.view.activity.MainActivity
import com.example.couroutinstudy.viewmodel.BaseViewModel
import java.io.Serializable
import java.text.SimpleDateFormat
import java.time.Year
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

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
        Log.d(TAG, "alarmMain go ")
//        viewModel = ViewModelProvider(activity as FragmentActivity)[BaseViewModel::class.java] 프래그먼트에서 뷰모델 생성 방법 1
//        viewModel = ViewModelProvider(activity as ViewModelStoreOwner)[BaseViewModel::class.java] 프래그먼트에서 뷰모델 생성 방법 2

        viewModel =
            ViewModelProvider(requireActivity())[BaseViewModel::class.java] //프래그먼트에서 뷰모델 생성 방법 3
        //프래그먼트에서 위의 방식과 같이 뷰모델을 생성하면 액티비티에서 생성한 뷰모델을 공유한다.
        //requireActivity는 getActivity가 null일 경우 IllegalStatementException을 던진다.
        alarm = Alarm()
        //취소버튼 클릭 이벤트
        binding.btnCancel.setOnClickListener {
            alarm =
                Alarm() // slidingView가 닫히고 다시 열릴 떄 onViewCreated를 타지 않게 했기 때문에 alarm객체를 다시 초기화한다.
//            viewModel.setAlarm(alarm)
            viewModel.updateTime(0, 0) // 알람객체의 hourOfDay와 minute를 0으로 초기화(Default = null)
            viewModel.closeSlide() //취소 버튼 클릭 시 슬라이드 닫음
        }

        //알람 저장 버튼 클릭 이벤트
        binding.btnAlarmSave.setOnClickListener {
            //알림 저장 버튼 클릭 시 실행되어야 할 코드 작성
            checkAlarmData() //alarm null Check
//            alarm?.let { alarm -> viewModel.setAlarm(alarm) }
            registAlarm(alarm)
            viewModel.insertAlarm(alarm)

            alarm =
                Alarm() // slidingView가 닫히고 다시 열릴 떄 onViewCreated를 타지 않게 했기 때문에 alarm객체를 다시 초기화한다.
//            viewModel.setAlarm(alarm)
            viewModel.updateTime(0, 0)
            viewModel.closeSlide()
        }




        viewModel.alarms.observe(requireActivity(), Observer {

        })
        binding.menuRepeat.setOnClickListener {//반복 버튼 클릭 이벤트
            val bundle = Bundle()
            checkAlarmData() //alarm null Check
            bundle.putSerializable("alarmInfo", alarm)
            mActivity.changeBundle(bundle)
            viewModel.changeFragment(DAY_OF_WEEK_FRAGMENT) //요일 선택 프래그먼트로 교체
        }

        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
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
            Log.d(TAG, "alarmObj observe: ")
            this.alarm = alarm
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView: ")
        //프래그먼트의 생명주기가 끝날 때 binding도 같이 삭제
        _binding = null
    }

    //TimePicker를 건드리지 않았을 때 alarm객체의
    //amPm과 time필드가 null이기 때문에 이 두 필드의
    //null체크를함과 동시에 초기화 해주는 메소드
    private fun checkAlarmData() {
        if (alarm.amPm == null && alarm.time == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "BuildVersion: Higher M ")
                alarm.amPm = if (binding.timePicker.hour < 12) "오전" else "오후"
                val hour: Any =
                    if (binding.timePicker.hour < 10) "0${binding.timePicker.hour}" else "${binding.timePicker.hour}"
                val minute: Any =
                    if (binding.timePicker.minute < 10) "0${binding.timePicker.minute}" else "${binding.timePicker.minute}"
                alarm.time = "${hour}:${minute}"
            } else {
                Log.d(TAG, "BuildVersion: lower M ")
                alarm.amPm = if (binding.timePicker.currentHour < 12) "오전" else "오후"
                val hour: Any =
                    if (binding.timePicker.currentHour < 10) "0${binding.timePicker.currentHour}" else "${binding.timePicker.currentHour}"
                val minute: Any =
                    if (binding.timePicker.currentMinute < 10) "0${binding.timePicker.currentMinute}" else "${binding.timePicker.currentMinute}"
                alarm.time =
                    "${hour}:${minute}"
            }
        }
    }

    fun registAlarm(alarm: Alarm) {

        val time = alarm.time
        val cal = Calendar.getInstance()
        val date = Date()
        cal.time = date
        val arr = time?.split(":")
        val hourOfDay = arr!!.get(0)
        val minute = arr!!.get(1)
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
        cal.set(Calendar.MINUTE, minute.toInt())


        Log.d(TAG, "registAlarm: ${activity.toString()}")
        Log.d(TAG, "registAlarm: ${alarm.dayOfWeek as ArrayList<DayOfWeek>}")

        alarmManager = activity?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(activity?.applicationContext, AlarmReceiver::class.java)
        val bundle = Bundle()
        bundle.putSerializable("myData",alarm)
        alarmIntent.putExtra("bundle",bundle)
        val pendingIntent :PendingIntent = PendingIntent.getBroadcast(activity?.applicationContext,400, alarmIntent, 0)


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
