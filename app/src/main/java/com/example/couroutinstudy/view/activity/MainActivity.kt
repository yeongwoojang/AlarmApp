package com.example.couroutinstudy.view.activity

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginBottom
import androidx.core.view.marginTop
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.ActivityMainBinding
import com.example.couroutinstudy.databinding.BottomSlidingViewBinding
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.util.SlidingUpPanelResizeAnimation
import com.example.couroutinstudy.view.adapter.AlarmAdapter
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.bottom_sliding_view.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivity : AppCompatActivity() {

    private var items = ArrayList<Alarm>()
    private lateinit var binding: ActivityMainBinding

    companion object{
        val TAG = MainActivity::class.java.simpleName
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val adapter = AlarmAdapter()
        binding.alarmRv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.alarmRv.adapter = adapter

        items.add(Alarm("PM", "12:01"))
        items.add(Alarm("PM", "12:01"))
        items.add(Alarm("PM", "12:01"))
        adapter.updateItems(items)

//        val screenHeight = resources.displayMetrics.heightPixels //디바이스의 height값

        binding.dragview.edtTime.let{
            it.isCursorVisible = false //시간 editText 커서 제거
               it.setText("00:00")
        }


        binding.btnAddAlarm.setOnClickListener { // + 버튼 클릭 이벤트
            //slidngPannel을 연다
            binding.slidingview.panelState = SlidingUpPanelLayout.PanelState.EXPANDED 
        }

        binding.dragview.btnCancel.setOnClickListener {// 취소 버튼 클릭 이벤트
            //slidngPannel을 닫는다
            binding.slidingview.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
        }

    }
}

