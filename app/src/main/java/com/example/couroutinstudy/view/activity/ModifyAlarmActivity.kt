package com.example.couroutinstudy.view.activity

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
import com.example.couroutinstudy.view.adapter.DayOfWeekAdapterForActi
import com.example.couroutinstudy.viewmodel.ModifyViewModel
import kotlinx.android.synthetic.main.fragment_day_of_week.*

class ModifyAlarmActivity : AppCompatActivity() {

    private lateinit var binding : ActivityModifyAlarmBinding
    private lateinit var viewModel : ModifyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyAlarmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[ModifyViewModel::class.java]//뷰모델 초기화

        val intent = intent
        val alarm = intent.getSerializableExtra("alarm") as Alarm // 알람목록중 하나 클릭 했을 시 넘겨받은 "Alarm"객체
        val adapter = DayOfWeekAdapterForActi(this,alarm,viewModel) //어댑터 설정

        binding.dayOfWeekRv.let {//리사이클러뷰, 설정
            it.adapter = adapter
            it.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false) //리사이클러뷰를 가로로 사용
            val animator = it.itemAnimator //RecyclerView를 update했을 시 화면이 깜빡이는 현상을 제거하는 부분
            if (animator is SimpleItemAnimator) animator.supportsChangeAnimations = false
        }
        binding.dayOfWeekRv.adapter = adapter //리사이클러뷰에 어댑터 장착


        binding.btnCancel.setOnClickListener {// 취소버튼 클릭 이벤트
            finish()
            overridePendingTransition(R.anim.none,R.anim.down_animation)
        }

        binding.btnAlarmModify.setOnClickListener { //수정버튼 클릭 이벤트

        }


        viewModel.updateDayOfWeekLD.observe(this, Observer {isUpdate->
            if(isUpdate){
                Log.d("safg", "sfgsfdg: 업데이트 완료")
            }
        })


    }
}