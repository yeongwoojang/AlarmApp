package com.example.couroutinstudy.view.fragment

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.couroutinstudy.databinding.FragmentAlarmMainBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
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
    private lateinit var mActivity : MainActivity
    private lateinit var alarm  : Alarm//알람정보를 저장할 객체
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
        Log.d(TAG, "onViewCreated: ")
//        viewModel = ViewModelProvider(activity as FragmentActivity)[BaseViewModel::class.java] 프래그먼트에서 뷰모델 생성 방법 1
//        viewModel = ViewModelProvider(activity as ViewModelStoreOwner)[BaseViewModel::class.java] 프래그먼트에서 뷰모델 생성 방법 2

        viewModel =
            ViewModelProvider(requireActivity())[BaseViewModel::class.java] //프래그먼트에서 뷰모델 생성 방법 3
        //프래그먼트에서 위의 방식과 같이 뷰모델을 생성하면 액티비티에서 생성한 뷰모델을 공유한다.
        //requireActivity는 getActivity가 null일 경우 IllegalStatementException을 던진다.
        alarm = Alarm()
        //취소버튼 클릭 이벤트
        binding.btnCancel.setOnClickListener {
            alarm = Alarm()
            viewModel.setAlarm(alarm)
            viewModel.updateTime(0,0)
            viewModel.closeSlide() //취소 버튼 클릭 시 슬라이드 닫음
        }

        //알람 저장 버튼 클릭 이벤트
        binding.btnAlarmSave.setOnClickListener {
            //알림 저장 버튼 클릭 시 실행되어야 할 코드 작성
            checkAlarmData() //alarm null Check
            alarm?.let { alarm -> viewModel.setAlarm(alarm) }
            viewModel.insertAlarm(alarm)

        }

        binding.menuRepeat.setOnClickListener {//반복 버튼 클릭 이벤트
            val bundle = Bundle()
            checkAlarmData() //alarm null Check
            bundle.putSerializable("alarmInfo", alarm)
            mActivity.changeBundle(bundle)
            viewModel.changeFragment(DAY_OF_WEEK_FRAGMENT) //요일 선택 프래그먼트로 교체
        }

        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            val amPm = if (hourOfDay < 12) "AM" else "PM" //선택한 시간이 오전인지 오후인지 식별
            val time = "${hourOfDay}:${minute}"
            alarm?.let{alarm->
                alarm.amPm = amPm
                alarm.time = time
            }
            viewModel.updateTime(hourOfDay, minute)
        }

        binding.btnActiveRepeatAlarm.setOnClickListener {
            //다시알림 메뉴를 활성화 했을 때 실행되어야 할 코드 작성
            alarm?.let{alarm->
                alarm.isRepeat = !alarm.isRepeat
                checkAlarmData() //alarm null Check
                viewModel.setAlarm(alarm)
            }

        }

        //프래그먼트가 내려갔다가 다시 올라와도 시간을 유지시키기 위한 timeLiveData observe
        viewModel.timeLd.observe(requireActivity(), Observer { time ->
            //API23버전 이전과 이후로 방법이 다르기 때문에 분기처리
            if(time.get("hourOfDay") !=0 && time.get("minute") !=0){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.timePicker.hour = time.get("hourOfDay")!!
                    binding.timePicker.minute = time.get("minute")!!
                } else {
                    binding.timePicker.currentHour = time.get("hourOfDay")!!
                    binding.timePicker.currentMinute = time.get("minute")!!
                }
            }else{
                val cal = Calendar.getInstance()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.timePicker.hour = cal.get(Calendar.HOUR_OF_DAY)
                    binding.timePicker.minute = cal.get(Calendar.MINUTE)
                }else{
                    binding.timePicker.currentHour =  cal.get(Calendar.HOUR_OF_DAY)
                    binding.timePicker.currentMinute = cal.get(Calendar.MINUTE)
                }

            }

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
    fun checkAlarmData(){
        alarm?.let{alarm->
            if (alarm.amPm == null && alarm.time == null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarm.amPm = if (binding.timePicker.hour < 12) "AM" else "PM"
                    alarm.time = "${binding.timePicker.hour}:${binding.timePicker.minute}"
                } else {
                    alarm.amPm = if (binding.timePicker.currentHour < 12) "AM" else "PM"
                    alarm.time =
                        "${binding.timePicker.currentHour}:${binding.timePicker.currentMinute}"
                }
            }
        }

    }
}
