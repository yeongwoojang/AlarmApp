package com.example.couroutinstudy.util.receiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompatExtras
import com.example.couroutinstudy.R
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.view.activity.TestActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.lang.NullPointerException

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
//            Toast.makeText(context?.applicationContext,"${context.toString()}",Toast.LENGTH_SHORT).show()
            val bundle = it.getBundleExtra("bundle")
            if (bundle != null) {
                val data: Alarm = bundle.getSerializable("myData") as Alarm
                val appName = "com.example.characterapp"
                notificationSetting(context?.applicationContext)
//                try{
//                    val launchInent = context?.packageManager?.getLaunchIntentForPackage(appName)
//                    val pi = PendingIntent.getActivity(context,0,launchInent,PendingIntent.FLAG_ONE_SHOT)
//                    pi.send()
//                }catch (e : PendingIntent.CanceledException){
//                    e.printStackTrace()
//                }catch (e : NullPointerException){
//                    Log.d("EXET", "앱이 설치되지 않음")
//                }


            }


        }

    }

    fun notificationSetting(context: Context?) {

        val player = MediaPlayer.create(context,R.raw.mom)
        var builder: NotificationCompat.Builder? = null
            val manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("aa", "aa", NotificationManager.IMPORTANCE_DEFAULT)// 중요도에 따라 메시지가 보이는 순서가 달라진다.
            channel.enableLights(true) //
            channel.lightColor = Color.RED
            channel.enableVibration(true)

            manager.createNotificationChannel(channel)
            builder = context?.let { NotificationCompat.Builder(it, "aa") }
        } else {
            builder = context?.let { NotificationCompat.Builder(it,"") }
        }
            builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("asd")
                .setContentText("Asds")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


//        CoroutineScope(Default).launch {
//            player.start()
//        }
        manager.notify(1234,builder?.build())
    }
}