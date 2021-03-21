package com.example.couroutinstudy.util.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.couroutinstudy.R
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.util.work.AlarmWorker
import com.example.couroutinstudy.view.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class AlarmReceiver : BroadcastReceiver() {
    private var alarmTime: String? = ""
    lateinit var manager: NotificationManager
    private var workManager : WorkManager? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            workManager = context?.let{WorkManager.getInstance(context)}
            manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (intent.action.equals("sendNotification")) {
                //            Toast.makeText(context?.applicationContext,"${context.toString()}",Toast.LENGTH_SHORT).show()
                val bundle = it.getBundleExtra("bundle")
                if (bundle != null) {
                    val data: Alarm = bundle.getSerializable("myData") as Alarm
                    alarmTime = data.time
                    setNotification(context?.applicationContext)
                }
            } else {
                Toast.makeText(context, "알림 중단!", Toast.LENGTH_SHORT).show()
                alarmCancel(context)
            }
        }
    }

    fun setNotification(context: Context?) {
        context?.let {
//            val player = MediaPlayer.create(context, R.raw.mom)
//            player.isLooping = true
            val clickIntent = Intent(context, AlarmReceiver::class.java)
            val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0)
            val notificationLayout = RemoteViews(context.packageName, R.layout.notification_custom)
            notificationLayout.setTextViewText(R.id.txt_noti_alarm_time, "${alarmTime}")
            notificationLayout.setOnClickPendingIntent(R.id.txt_pause, clickPendingIntent)
            var builder: NotificationCompat.Builder? = null

            val intent = Intent(context, MainActivity::class.java)

            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "alarmChannel"
                val channelName = "alarm"
                val importance = NotificationManager.IMPORTANCE_HIGH // 중요도에 따라 메시지가 보이는 순서가 달라진다.

                val channel = NotificationChannel(channelId, channelName, importance)
                channel.enableLights(true) //
                channel.lightColor = Color.RED
                channel.enableVibration(true)

                manager.createNotificationChannel(channel)
                builder = NotificationCompat.Builder(it, channelId)
            } else {
                builder = NotificationCompat.Builder(it, "")
            }
            val notification = builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setCustomContentView(notificationLayout)
                .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                .setCustomBigContentView(notificationLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setFullScreenIntent(pendingIntent, true)
                .build()

            val workRequest =
                OneTimeWorkRequestBuilder<AlarmWorker>()
                    .addTag("player")
                    .build()

            workManager?.enqueue(workRequest)
            manager.notify(1234, notification)
            Log.d("AlarmWorker", "setNotification: ${workManager.toString()}")
        }
    }

    fun alarmCancel(context: Context?) {
        context?.let{
            manager.cancel(1234)
            workManager?.cancelAllWorkByTag("player")
            Log.d("AlarmWorker", "setNotification: ${workManager.toString()}")

        }

    }
}