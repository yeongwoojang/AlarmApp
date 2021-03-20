package com.example.couroutinstudy.util.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.couroutinstudy.R
import com.example.couroutinstudy.model.vo.Alarm

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            val bundle = it.getBundleExtra("bundle")
            if (bundle != null) {
                val data: Alarm = bundle.getSerializable("myData") as Alarm
            }
            Log.d("onReceive", "onReceive: ")
                notificationSetting(context)

        }

    }

    fun notificationSetting(context: Context?) {
        var builder: NotificationCompat.Builder? = null
            val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("aa", "aa", NotificationManager.IMPORTANCE_DEFAULT)// 중요도에 따라 메시지가 보이는 순서가 달라진다.
            channel.enableLights(true) //
            channel.lightColor = Color.RED
            channel.enableVibration(true)
            manager.createNotificationChannel(channel)
            builder = context?.let { NotificationCompat.Builder(it, "aa") }
            builder.setSmallIcon(R.drawable.icon_check)

        } else {
            builder = context?.let { NotificationCompat.Builder(it,"a") }
            builder.setSmallIcon(R.mipmap.ic_launcher)
        }
        manager.notify(1234,builder?.build())
    }
}