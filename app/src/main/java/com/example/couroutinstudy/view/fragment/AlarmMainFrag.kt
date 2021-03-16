package com.example.couroutinstudy.view.fragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.couroutinstudy.databinding.FragmentAlarmMainBinding
import com.example.couroutinstudy.viewmodel.BaseViewModel

class AlarmMainFrag : Fragment() {

    companion object {
        val TAG = AlarmMainFrag.javaClass.simpleName
        const val DAY_OF_WEEK_FRAGMENT: Int = 1
    }

    private var _binding: FragmentAlarmMainBinding? = null //binding 객체

    private val binding get() = _binding!! //binding객체 getter

    private lateinit var viewModel: BaseViewModel //뷰모델 객체

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

        binding.btnCancel.setOnClickListener {

        }

        binding.btnAdd.setOnClickListener {
            viewModel.openSlide()
        }

        binding.menuRepeat.setOnClickListener {//반복 버튼 클릭 이벤트
            Log.d(TAG, "반복 버튼 클릭")
            viewModel.changeFragment(DAY_OF_WEEK_FRAGMENT) //요일 선택 프래그먼트로 교체
        }

        binding.timePicker.setOnTimeChangedListener { view, hourOfDay, minute ->
            viewModel.updateTime(hourOfDay, minute)
        }


        //프래그먼트가 내려갔다가 다시 올라와도 시간을 유지시키기 위한 timeLiveData observe
        viewModel.timeLd.observe(requireActivity(), Observer { time ->
            //API23버전 이전과 이후로 방법이 다르기 때문에 분기처리


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.timePicker.hour = time.get("hourOfDay")!!
                binding.timePicker.minute = time.get("minute")!!
            } else {
                binding.timePicker.currentHour = time.get("hourOfDay")!!
                binding.timePicker.currentMinute = time.get("minute")!!
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //프래그먼트의 생명주기가 끝날 때 binding도 같이 삭제
        _binding = null
    }

}