package com.example.couroutinstudy.util.work

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class AlarmWorker(appContext : Context, workParams :WorkerParameters) :Worker(appContext,workParams){

    override fun doWork(): Result {

        return Result.success()
    }
}