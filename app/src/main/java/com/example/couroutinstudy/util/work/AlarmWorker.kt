package com.example.couroutinstudy.util.work

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.couroutinstudy.R

class AlarmWorker(appContext : Context, workParams :WorkerParameters) :Worker(appContext,workParams){
    val player = MediaPlayer.create(applicationContext, R.raw.mom)
    override fun doWork(): Result {


        player.isLooping = true
        player.start()
        for (i in 0..30){
            Thread.sleep(1000)
            Log.d("doWork", "doWork: ${i}")
        }
        return Result.success()
    }

    override fun onStopped() {
        super.onStopped()
        Log.d("AlarmWorker", "onStopped: ")
        player.stop()
    }
}