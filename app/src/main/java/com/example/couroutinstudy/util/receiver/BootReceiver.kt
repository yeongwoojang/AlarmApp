package com.example.couroutinstudy.util.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.couroutinstudy.database.AppDatabase
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.view.fragment.AlarmMainFrag
import java.util.*

class BootReceiver : BroadcastReceiver() {

    var db: AppDatabase? = null
    private lateinit var alarm: Alarm//알람정보를 저장할 객체?
    private var alarmManager: AlarmManager? = null
    private lateinit var mContext : Context
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action=="android.intent.action.BOOT_COMPLETED") {
            context?.let {
                db = AppDatabase.getInstance(it)
                mContext = it
                val alarmList: List<Alarm> = db!!.alarmDao().bootGetAll()
                val cal = Calendar.getInstance()
                val curDayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
                val index = if (curDayOfWeek != 1) curDayOfWeek - 2 else 6

                for (i in 0..alarmList.size - 1) {
                    if (alarmList[i].isOn && alarmList[i].dayOfWeek[index].requestCode!=-1){
                        alarm = alarmList[i]
                        val time = alarmList[i].time //Alarm 객체에 담긴 시간을 calendar 객체에 지정
                        Log.d(AlarmMainFrag.TAG, "registerAlarm: ${time}")
                        val arr = time?.split(":")
                        val hourOfDay = arr!!.get(0) //알람이 울릴 "시간"
                        val minute = arr!!.get(1) //알람이 울릴 "분"

                        alarmManager =
                            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                        val alarmIntent =
                            Intent(context.applicationContext, AlarmReceiver::class.java)
                        alarmIntent.action = "sendNotification"

                        cal.set(Calendar.DAY_OF_WEEK,curDayOfWeek)
                        // Calendar 객체에 Alarm 객체에 체크된 요일을 Setting
                        // Calendar 객체에 알람이 울릴 시간과 분을 지정`
                        cal.set(Calendar.HOUR_OF_DAY, hourOfDay.toInt())
                        cal.set(Calendar.MINUTE, minute.toInt())
                        setPendingIntent(alarmManager, alarmIntent, cal, index) //PendingIntent 설정
                    }
                }

            }
        }
    }
    //intent에 담을 값과 penndingIntent 세팅
    private fun setPendingIntent( alarmManager: AlarmManager?,alarmIntent: Intent,cal: Calendar,index: Int){
        var position = index

        val bundle = Bundle()
        bundle.putSerializable("alarmData", alarm)
        bundle.putSerializable("alarmDate", cal)
        alarmIntent.putExtra("bundle", bundle)
        if(position==-1){
            val calendar = Calendar.getInstance()
            val dayOfWeekCode = calendar.get(Calendar.DAY_OF_WEEK) //오늘의 요일을 구한다.
            position = if (dayOfWeekCode != 1) dayOfWeekCode - 2 else 6 //요일에 맞는 인덱스 설정
        }

        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            mContext.applicationContext
            , alarm.dayOfWeek[position].requestCode
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