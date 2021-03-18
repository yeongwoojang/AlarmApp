package com.example.couroutinstudy.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.couroutinstudy.R
import com.example.couroutinstudy.model.vo.Alarm

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        
        val a = intent.getSerializableExtra("day") as Alarm
        Log.d("시발", "onCreate: ${a.toString()}")
    }
}