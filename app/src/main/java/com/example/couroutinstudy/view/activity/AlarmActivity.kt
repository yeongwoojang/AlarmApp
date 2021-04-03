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
    private var workManager : WorkManager? = null
    private lateinit var binding: ActivityAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlarmBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        turnScreenOnAndKeyguardOff()

        val valueAnimator = ValueAnimator.ofInt(0, 300) // "ProgressBar" 범위 지정
        binding.circleProgressbar.max = 300 //"ProgressBar" 최댓 값 지정
        
        valueAnimator.addUpdateListener { animation -> //value값 변경시  "ProgressBar" 움직게하는 리스너
            val progress = animation.animatedValue as Int
            binding.circleProgressbar.progress = progress
        }
        valueAnimator.setDuration(30000) //약 30초동안 "ProgressBar" 진행

        val workRequest =
            OneTimeWorkRequestBuilder<TestWorker>()
                .addTag("player")
                .build()

        workManager = WorkManager.getInstance(this) //"WorkManager" 싱글톤 객체 호출

         //알람 울리기 시작
//        CoroutineScope(Dispatchers.Default).launch {
//            workManager?.enqueue(workRequest)
//        }
        valueAnimator.start() //"ProgressBar" 시작
        

        binding.cancelBtn.setOnClickListener {
            workManager?.cancelAllWorkByTag("player")
            valueAnimator.cancel()

        }

        WorkManager.getInstance(this).getWorkInfosByTagLiveData("player")
            .observe(this, Observer {workInfo :List<WorkInfo?> ->
                Log.d("HEMML", "onCreate: ${workInfo}")
                if(workInfo[0] != null){
//                    val value = workInfo.outputData.getInt("Progress",777)
                    Log.d("HIHIHI", "onCreate: ${workInfo[0]?.state}")
                    if(workInfo[0]?.state == WorkInfo.State.CANCELLED){
                        val value = workInfo[0]?.progress?.getInt("Progress",777)
                        Log.d("FROM", "onCreate: $value")
                        if(value!=777){
//                            Log.d("FROM", "onCreate: $value")
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