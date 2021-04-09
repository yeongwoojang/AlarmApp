package com.example.couroutinstudy.view.activity

import android.animation.ValueAnimator
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.couroutinstudy.databinding.ActivityAlarmBinding
import com.example.couroutinstudy.util.work.AlarmWorker
import com.example.couroutinstudy.util.work.TestWorker
import com.example.couroutinstudy.util.work.TestWorker.Companion.Progress
import kotlinx.coroutines.*

class AlarmActivity : AppCompatActivity() {
    private var workManager: WorkManager? = null
    private lateinit var binding: ActivityAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        turnScreenOnAndKeyguardOff()

//        val valueAnimator = ValueAnimator.ofInt(0, 300) // "ProgressBar" 범위 지정
        binding.circleProgressbar.max = 300 //"ProgressBar" 최댓 값 지정
//
//
//        valueAnimator.addUpdateListener { animation -> //value값 변경시  "ProgressBar" 움직게하는 리스너
//            val progress = animation.animatedValue as Int
//            binding.circleProgressbar.progress = progress
//        }
//        valueAnimator.setDuration(30000) //약 30초동안 "ProgressBar" 진행
//        valueAnimator.start() //"ProgressBar" 시작

        val workRequest =
            OneTimeWorkRequestBuilder<TestWorker>()
                .addTag("player")
                .build()

        workManager = WorkManager.getInstance(this) //"WorkManager" 싱글톤 객체 호출

        //알람 울리기 시작
//        CoroutineScope(Dispatchers.Default).launch {
//            workManager?.enqueue(workRequest)
//        }


        binding.cancelBtn.setOnClickListener {
            workManager?.cancelAllWorkByTag("player")
//            valueAnimator.cancel()

        }

        WorkManager.getInstance(this).getWorkInfosByTagLiveData("player")
            .observe(this, Observer { info: List<WorkInfo?> ->
                var workInfo : WorkInfo? = null
                for(i in 0.. info.size-1){
                    if(info[i]?.state==WorkInfo.State.RUNNING){
                         workInfo = info[i]
                    }
                }
                if (workInfo != null) {
                    if (workInfo.state != WorkInfo.State.CANCELLED) {
                        val value = workInfo.progress.getInt("Progress", 777)
                        if (value != 777) {
                            val progress =value
                            binding.circleProgressbar.progress = progress
                        }
                    }

                    /*  Log.d("FROM", "onCreate:  ${workInfo?.state}")
                      if(workInfo?.state== WorkInfo.State.CANCELLED){
                          Log.d("FROM", "onCreate: 취소됨")
                      }
                      if(workInfo?.state== WorkInfo.State.RUNNING){

                      }
                      if(workInfo?.state!= WorkInfo.State.ENQUEUED){
                          val progress = workInfo.progress
                          val value = progress.getInt("Progress",777)
                          if(value!=777){
                             Log.d("FROM", "onCreate: $value")
                          }
                      }*/

                }

            })


//        workManager.cancelAllWorkByTag("player")


    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    //화면이 꺼져있을 때 액티비티가 실행될 때 화면을 깨우기 위한 메소드
    fun Activity.turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
            )
        }
        with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
            }
        }
    }
}