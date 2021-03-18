package com.example.couroutinstudy.util.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.couroutinstudy.model.vo.Alarm
import com.example.couroutinstudy.model.vo.DayOfWeek
import com.example.couroutinstudy.model.vo.Test
import java.io.Serializable

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let{
            val bundle = it.getBundleExtra("bundle")
            if(bundle!=null){
                val data : Alarm = bundle.getSerializable("myData") as Alarm
                Log.d("receiver", "onReceive: $data")
            }
        }

    }
}