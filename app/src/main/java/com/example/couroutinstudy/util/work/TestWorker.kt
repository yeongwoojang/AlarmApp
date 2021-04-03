package com.example.couroutinstudy.util.work

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import androidx.work.*
import com.example.couroutinstudy.R
import kotlinx.coroutines.*
import java.util.concurrent.Executor

class TestWorker(appContext: Context, workParam: WorkerParameters) :
    CoroutineWorker(appContext, workParam) {

    val player = MediaPlayer.create(applicationContext, R.raw.mom)

    //    var count: Data = workDataOf(Progress to 0)
    var playerStop = false

    companion object {
        const val Progress = "Progress"
    }


    override suspend fun doWork(): Result {

        runBlocking {
            val job = CoroutineScope(Dispatchers.Default).launch {
                player.isLooping = true // 벨소리 player 무한반복
                player.start() // 벨소리 시작
                for (i in 0..30) {
                    val count = workDataOf(Progress to i)
                    setProgress(count)
                    delay(1000)
                }
            }
            job.start()
            if (job.isCancelled) {
                Log.d("SFDgsdf", "doWork: fdgsg")
                player.stop()
            }
        }
        if (player.isPlaying) {
            player.stop()
        }
        return Result.success()

        return Result.success()
    }

}