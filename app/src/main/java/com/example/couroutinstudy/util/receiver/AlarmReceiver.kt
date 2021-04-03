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
import com.example.couroutinstudy.database.AppDatabase
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.util.work.AlarmWorker
import com.example.couroutinstudy.util.work.TestWorker
import com.example.couroutinstudy.view.activity.AlarmActivity
import com.example.couroutinstudy.view.activity.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        val TAG = AlarmReceiver.javaClass.simpleName
    }

    var db: AppDatabase? = null

    private var alarmTime: String? = ""
    lateinit var manager: NotificationManager
    private var workManager: WorkManager? = null
    private var alarmManager: AlarmManager? = null
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            workManager = context?.let { WorkManager.getInstance(context) }
            db = context?.let { AppDatabase.getInstance(context) }
            manager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (intent.action.equals("sendNotification")) { //알람을 보내겠다는 액션이 들어왔을 시
                val bundle = it.getBundleExtra("bundle")
                if (bundle != null) { //넘어온 Bundle이 null이 아니라면
                    val alarm: Alarm = bundle.getSerializable("alarmData") as Alarm //등록한 알람 정보
                    val alarmDate =
                        bundle.getSerializable("alarmDate") as Calendar // 알람이 울리는 시간(Calendar 객체)
                    Log.d(TAG, "onReceive 알람시간: ${alarmDate.time}")
                    val currentDate = Calendar.getInstance() //현재시간 정보를 담고있는 "Calendar"객체
                    var dayOfWeek: String? = ""
                    //등록된 알람이 onReceive를 탔을 때 몇요일 알람이 넘어온지 확인
                    Log.d(TAG, "onReceive요일: ${alarmDate.time}")

                    when (alarmDate.get(Calendar.DAY_OF_WEEK)) {
                        1 -> dayOfWeek = "일"
                        2 -> dayOfWeek = "월"
                        3 -> dayOfWeek = "화"
                        4 -> dayOfWeek = "수"
                        5 -> dayOfWeek = "목"
                        6 -> dayOfWeek = "금"
                        7 -> dayOfWeek = "토"
                    }
                    Log.d(TAG, "onReceive: ${dayOfWeek}요일")

                    val timeDifference = currentDate.timeInMillis - alarmDate.timeInMillis
                    val min = timeDifference / (60 * 1000)
                    val hour = timeDifference / (60 * 60 * 1000)
                    val day = timeDifference / (24 * 60 * 60 * 1000)
                    val defDay = day.toInt()
                    val defMin = (min - hour * 60).toInt()
                    val defHour = (hour - day * 24).toInt()
                    //넘어온 알람의 시간과 현재시간의 차이를 알아보기 위한 로그
                    Log.d(TAG, "onReceive 일 차이: ${defDay}일")
                    Log.d(TAG, "onReceive 일 차이: ${defHour}시간")
                    Log.d(TAG, "onReceive 분 차이: ${defMin}분")
                    if (defMin == 0 && defDay == 0 && defHour == 0) {
                        alarmTime = alarm.time
                        sendNotification(context.applicationContext) //알람 노티피케이션을 띄운다.
                    } else {
                        //요일 반복알람을 등록했을 때 이미 지난 날짜일 경우 울리지 않게함
                        //ex) 일요일 반복 알람을 등록했을 때 현재 월요일인 경우 앞 날짜의 일요일이 예약되는 것이 아니라
                        // 전날 일요일이 예약이 되어버린다. 따라서 이 경우는 다음 주 일요일 알람만 재지정해두고 "Notification"을 띄우지 않는다.
                        Log.d(TAG, "onReceive: 알람 시간과 현재시간이 맞지않아 알람이 울리지 않음")
                        Log.d(TAG, "onReceive 데이오브 위크: ${alarm.dayOfWeek}")
                    }

                    //Bundle로 넘어온 Alarm 객체에 몇요일 마다 알람이 울리게 설정 되어있는지 체크
                    for (i in 0..6) {
                        if (alarm.dayOfWeek[i].isCheck) { // Alarm 객체에  해당 요일 체크되어 있다면
                            if (alarmDate.get(Calendar.DAY_OF_WEEK) == i + 2 && alarmDate.get(
                                    Calendar.DAY_OF_WEEK
                                ) != 1
                            ) {
                                //오늘과 같은 요일에 체크되어있는데 일요일이 아닐 시
                                Log.d(
                                    TAG,
                                    "onReceive언제 알림등록: ${alarmDate.get(Calendar.DAY_OF_WEEK)}"
                                )
                                registerAlarm(
                                    alarm,
                                    alarmDate,
                                    context
                                ) //다음 주 같은 시간 같은 요일에 다시 알람 등록
                                break
                            } else if (alarmDate.get(Calendar.DAY_OF_WEEK) == 1 && i == 6) {
                                //오늘과 같은 요일에 체크되어있는데 일요일 일시
                                Log.d(
                                    TAG,
                                    "onReceive언제 알림등록: ${alarmDate.get(Calendar.DAY_OF_WEEK)}"
                                )
                                registerAlarm(
                                    alarm,
                                    alarmDate,
                                    context
                                ) //다음 주 같은 시간 같은 요일에 다시 알람 등록
                                break
                            }
                        }
                    } //체크된 요일이 없다면 그냥 넘어간다.

                }
            } else if (intent.action.equals("removeNotification")) {
                alarmCancel(context) //알림 중지
            }
        }
    }

    private fun sendNotification(context: Context?) {
        context?.let {
//            val player = MediaPlayer.create(context, R.raw.mom)
//            player.isLooping = true
            val clickIntent = Intent(context, AlarmReceiver::class.java)
            clickIntent.action = "removeNotification"
            val clickPendingIntent = PendingIntent.getBroadcast(context, 0, clickIntent, 0)
            val notificationLayout =
                RemoteViews(context.packageName, R.layout.notification_custom)
            notificationLayout.setTextViewText(R.id.txt_noti_alarm_time, "${alarmTime}")
            notificationLayout.setOnClickPendingIntent(R.id.txt_pause, clickPendingIntent)
            var builder: NotificationCompat.Builder? = null

            val intent = Intent(context, AlarmActivity::class.java)
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
    private fun alarmCancel(context: Context?) {
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
    private fun registerAlarm(alarm: Alarm, cal: Calendar, context: Context) {
        Log.d(TAG, "onReceive: 등록")
        Log.d(TAG, "onReceive: onReceive로 전달 받은 시간 ${cal.time}")
        val pId = (Math.random() * 100000000).toInt()
        cal.add(Calendar.DATE, 7) //다음주에도 같은 시간에 알람 예약
        Log.d(TAG, "onReceive: 알람이 등록되는 시간 ${cal.time}")
//        cal.set(Calendar.SECOND,0)
//        cal.set(Calendar.MILLISECOND,0)
        val curDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) //현재 몇요일인지 구한다.
        val reRegisterDay = if (curDayOfWeek != 1) curDayOfWeek - 2 else 6 //요일 코드를 인덱스 값에 맞춰준다.
        alarm.dayOfWeek[reRegisterDay].requestCode = pId // Alarm 객체의 해당요일에 requestCode를 새로 할당한다.

        CoroutineScope(IO).launch {
            db!!.alarmDao().updateDayOfWeek(alarm.dayOfWeek, alarm.id)
        }


        alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val alarmIntent = Intent(context.applicationContext, AlarmReceiver::class.java)
        alarmIntent.action = "sendNotification"

        val bundle = Bundle()
        bundle.putSerializable("alarmData", alarm) //Alarm객체를  리시버에 넘긴다.
        bundle.putSerializable(
            "alarmDate",
            cal
        ) // 알람이 울렸던 시간에서 일주일을 더한 시간을 담고있는 Calendar 객체를 리시버에 넘긴다.

        alarmIntent.putExtra("bundle", bundle)
        val pendingIntent: PendingIntent =
            PendingIntent.getBroadcast(
                context.applicationContext
                , alarm.dayOfWeek[reRegisterDay].requestCode
                , alarmIntent
                , PendingIntent.FLAG_CANCEL_CURRENT
            )


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