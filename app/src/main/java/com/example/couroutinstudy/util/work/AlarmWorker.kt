package com.example.couroutinstudy.util.work

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.work.*
import com.example.couroutinstudy.R
import kotlinx.coroutines.delay

class AlarmWorker(appContext: Context, workParams: WorkerParameters) :
    Worker(appContext, workParams) {
    val player = MediaPlayer.create(applicationContext, R.raw.mom) //벨소리를 설정하는 객체
    var playerStop = false

    override fun doWork(): Result {
        player.isLooping = true // 벨소리 player 무한반복
        player.start() // 벨소리 시작
        for (i in 0..30) {
            val count = workDataOf("Progress" to i)
            setProgressAsync(count)
            Thread.sleep(1000)
            if (!player.isPlaying) {
                playerStop = true
            }
        }
        if (playerStop) {
            return Result.success()
        }
        player.stop()
        return Result.success()
    }

    override fun onStopped() { //work가 cancel 되었을 때 실행되는 메소드
        super.onStopped()
        player.stop() // 알람을 멈춤
    }
}