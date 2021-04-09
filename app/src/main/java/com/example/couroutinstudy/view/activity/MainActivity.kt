package com.example.couroutinstudy.view.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemAnimator
import androidx.recyclerview.widget.SimpleItemAnimator
import androidx.work.WorkManager
import com.example.couroutinstudy.R
import com.example.couroutinstudy.database.AppDatabase
import com.example.couroutinstudy.databinding.ActivityMainBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import com.example.couroutinstudy.util.receiver.AlarmReceiver
import com.example.couroutinstudy.view.adapter.AlarmAdapter
import com.example.couroutinstudy.view.fragment.AlarmMainFrag
import com.example.couroutinstudy.view.fragment.DayOfWeekFrag
import com.example.couroutinstudy.viewmodel.BaseViewModel
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var bundle: Bundle? = null
//    viewModel을 늦은 초기화 하기위한 벙법
//    private lateinit var viewModel : BaseViewModel


    companion object {
        val TAG = MainActivity::class.java.simpleName //로그를 찍기 위한 변수

        // 어떤 프래그먼트로 이동할 지 결정하는 FLAG
        const val ALARM_MAIN_FRAGMENT = 0
        const val DAY_OF_WEEK_FRAGMENT = 1
        const val SOUNT_FRAGMENT = 2
    }
    private lateinit var viewModel: BaseViewModel //androidx.activity 패키지에 정의된 함수를 이용한 뷰모델 초기화 방법
    private lateinit var fragmentManager: FragmentManager
    private lateinit var fragmentTransaction: FragmentTransaction
    private lateinit var alarmMainFrag: AlarmMainFrag
    private lateinit var dayOfWeekFrag: DayOfWeekFrag

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "MainActivitySequence onStart: ")

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivitySequence onCreate: ")
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this)[BaseViewModel::class.java]
        fragmentManager = supportFragmentManager //프래그먼트매니저 초기회
        alarmMainFrag = AlarmMainFrag()
        dayOfWeekFrag = DayOfWeekFrag()
        initFragment(alarmMainFrag) //초기 slidingView 설정

        //context를 사용 가능한 시점에서 늦은 뷰모델 초기화 방법
//        viewModel = ViewModelProvider(this)[BaseViewModel::class.java]
        val adapter = AlarmAdapter(this, viewModel)
        binding.alarmRv.let {
            it.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            it.adapter = adapter

            val animator = it.itemAnimator //RecyclerView를 update했을 시 화면이 깜빡이는 현상을 제거하는 부분
            if (animator is SimpleItemAnimator) animator.supportsChangeAnimations = false
        }

        val screenHeight = resources.displayMetrics.heightPixels //디바이스의 height값

        binding.slidingview.let {
            it.isTouchEnabled = false
            it.isTouchEnabled = false
        }

        binding.btnAddAlarm.setOnClickListener { // + 버튼 클릭 이벤트
            //slidngPannel을 연다
            viewModel.openSlide()
        }

        viewModel.slideLd.observe(this, Observer { //슬라이드 여부를 관찰하면서 뷰를 올릴지 내릴지 결정
            Log.d(TAG, "slideLd: ${it}")
            if (it == false) {
//                removeFragment(alarmMainFrag)
                viewModel.setAlarm(Alarm())
                binding.slidingview.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            } else {
//                initFragment(alarmMainFrag)
                binding.slidingview.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }
        })


        viewModel.fragIdLd.observe(this, Observer { fragId ->
            Log.d(TAG, "fragIdLd: ${fragId}")
            when (fragId) {
                ALARM_MAIN_FRAGMENT -> {
                    initFragment(alarmMainFrag)
                }
                DAY_OF_WEEK_FRAGMENT -> {
                    initFragment(dayOfWeekFrag)
                }
                SOUNT_FRAGMENT -> {
                    TODO("사운드 프래그먼트가 올 곳")
                }
            }
        })

        viewModel.alarmLd.observe(this, Observer {
            Log.d(AlarmMainFrag.TAG, "sequence alarmLd: MainActivity :")
        })

        viewModel.alarms.observe(this, Observer { alarmList ->
            Log.d(TAG, "UpdateAlarm!: $alarmList")
            adapter.updateItems(alarmList)
        })

    }

    override fun onBackPressed() {
        if (binding.slidingview.panelState == SlidingUpPanelLayout.PanelState.COLLAPSED){
            super.onBackPressed()
        }
        if (supportFragmentManager.backStackEntryCount == 1) {
            binding.slidingview.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            Log.d(TAG, "onBackPressed: cnt=1")

        } else {
            Log.d(TAG, "onBackPressed: cnt=0")
            super.onBackPressed()
        }
    }

    fun initFragment(fragment: Fragment) { //dragView로 설정되어있는 뷰를 다른 프래그먼트로 끼워주는 메소드
        fragmentTransaction = fragmentManager.beginTransaction() //프래그먼트 트랜잭션 초기화
        fragmentTransaction.setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_right,
            R.anim.enter_from_right,
            R.anim.exit_to_right
        )
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.replace(R.id.dragview, fragment)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun removeFragment(fragment: Fragment) {
        fragmentTransaction = fragmentManager.beginTransaction() //프래그먼트 트랜잭션 초기화
        fragmentTransaction.remove(fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commitAllowingStateLoss()
    }

    fun changeBundle(bundle: Bundle) {
        this.bundle = bundle
    }

    override fun onResume() {
        Log.d(TAG, "MainActivitySequence onResume: ")
        super.onResume()

    }

    override fun onDestroy() {
        Log.d(TAG, "MainActivitySequence onDestroy: ")
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }
}

