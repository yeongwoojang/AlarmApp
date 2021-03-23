package com.example.couroutinstudy.util.receiver

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.couroutinstudy.R
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.util.work.AlarmWorker
import com.example.couroutinstudy.view.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import java.text.SimpleDateFormat
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object{
        val TAG = AlarmReceiver.javaClass.simpleName
    }
    private var alarmTime: String? = ""
    lateinit var manager: NotificationManager
    private var workManager: WorkManager? = null
    private var alarmManager: AlarmManager? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            workManager = context?.let { WorkManager.getInstance(context) }
            manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (intent.action.equals("sendNotification")) { //알람을 보내겠다는 액션이 들어왔을 시

                val bundle = it.getBundleExtra("bundle")
                if (bundle != null) { //넘어온 Bundle이 null이 아니라면
                    val alarm: Alarm = bundle.getSerializable("alarmData") as Alarm //등록한 알람 정보
                    val alarmDate = bundle.getSerializable("alarmDate") as Calendar // 알람이 울리는 시간(Calendar 객체)
                    val currentDate = Calendar.getInstance() //현재시간 정보를 담고있는 "Calendar"객체
//                    val date = Date()
//                    currentDate.time = date
                    val compare = (currentDate.timeInMillis- alarmDate.timeInMillis)/60000
                    Log.d(TAG, "onReceive Today: ${currentDate.time}")
                    Log.d(TAG, "onReceive Alarm: ${alarmDate.time}")
                    Log.d(TAG, "onReceive 차이: ${compare}")
                    currentDate.get(Calendar.HOUR_OF_DAY)
                    currentDate.get(Calendar.MINUTE)
                    Log.d(TAG, "onReceive: ${(alarmDate.timeInMillis)*24*60*60*1000} 분")

                    Log.d(TAG, "onReceive: ${currentDate.get(Calendar.DATE)}")
                    Log.d(TAG, "onReceive: ${alarmDate.get(Calendar.MINUTE)}")

                    if((currentDate.get(Calendar.MINUTE)) - (alarmDate.get(Calendar.MINUTE))==0
                        && currentDate.get(Calendar.DATE) >= alarmDate.get(Calendar.DATE)){
                        alarmTime = alarm.time
                        sendNotification(context.applicationContext) //알람 노티피케이션을 띄운다.
                    }else{
                        //요일 반복알람을 등록했을 때 이미 지난 날짜일 경우 울리지 않게함
                        //ex) 일요일 반복 알람을 등록했을 때 현재 월요일인 경우 앞 날짜의 일요일이 예약되는 것이 아니라
                        // 전날 일요일이 예약이 되어버린다. 따라서 이 경우는 다음 주 일요일 알람만 재지정해두고 "Notification"을 띄우지 않는다.
                        Log.d(TAG, "onReceive: 이미 지난시간이라 알람 안울림")
                    }

                    //Bundle로 넘어온 Alarm 객체에 몇요일 마다 알람이 울리게 설정 되어있는지 체크
                        for (i in 0..6) {
                            if (alarm.dayOfWeek[i].isCheck) { // Alarm 객체에  해당 요일 체크되어 있다면
                                if (currentDate.get(Calendar.DAY_OF_WEEK) == i + 2 && currentDate.get(Calendar.DAY_OF_WEEK) != 1) {
                                    //오늘과 같은 요일에 체크되어있는데 일요일이 아닐 시
                                    registerAlarm(alarm,alarmDate,context) //다음 주 같은 시간 같은 요일에 다시 알람 등록
                                    break
                                }else if(currentDate.get(Calendar.DAY_OF_WEEK)==1 && i==6){
                                    //오늘과 같은 요일에 체크되어있는데 일요일 일시
                                    registerAlarm(alarm, alarmDate, context) //다음 주 같은 시간 같은 요일에 다시 알람 등록
                                    break
                                }
                            }
                        } //체크된 요일이 없다면 그냥 넘어간다.

                }
            } else {
                alarmCancel(context) //알림 중지
            }
        }
    }

    private fun sendNotification(context: Context?) {
        context?.let {
//            val player = MediaPlayer.create(context, R.raw.mom)
//            player.isLooping = true
            val clickIntent = Intent(context, AlarmReceiver::class.java)
            val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0)
            val notificationLayout =
                RemoteViews(context.packageName, R.layout.notification_custom)
            notificationLayout.setTextViewText(R.id.txt_noti_alarm_time, "${alarmTime}")
            notificationLayout.setOnClickPendingIntent(R.id.txt_pause, clickPendingIntent)
            var builder: NotificationCompat.Builder? = null

            val intent = Intent(context, MainActivity::class.java)

            val pendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channelId = "alarmChannel"
                val channelName = "alarm"
                val importance =
                    NotificationManager.IMPORTANCE_HIGH // 중요도에 따라 메시지가 보이는 순서가 달라진다.

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
        }
    }

    //알람 중지 메소드
    fun alarmCancel(context: Context?) {
        context?.let {
            manager.cancel(1234)
            workManager?.cancelAllWorkByTag("player")
        }

    }

    /*
    * alarm : "Bundle"로 넘어온 Alarm 객체
    * cal : 현재시간 정보를 담고있는 Calendar 객체 (등록했던 알람이 울린 시간을 담고있다.)
    * context : context 정보
    * */
    private fun registerAlarm(alarm : Alarm,cal: Calendar,context : Context) {
        Log.d(TAG, "onReceive: 등록")
        val pId = (Math.random()*100000000).toLong()
        cal.add(Calendar.DATE,7) //다음주에도 같은 시간에 알람 예약
         alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
        alarmIntent.action = "sendNotification"

        val bundle = Bundle()
        bundle.putSerializable("alarmData",alarm) //Alarm객체를 넘긴다 리시버에 넘긴다.
        bundle.putSerializable("alarmDate", cal) // 알람이 울렸던 시간에서 일주일을 더한 시간을 담고있는 Calendar 객체를 리시버에 넘긴다.
        alarmIntent.putExtra("bundle",bundle)
        val pendingIntent :PendingIntent = PendingIntent.getBroadcast(context.applicationContext,pId.toInt(), alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pendingIntent
            )
        } else {
            alarmManager?.setExact(
                AlarmManager.RTC_WAKEUP,
                cal.timeInMillis,
                pendingIntent
            )
        }
    }
}