package com.example.couroutinstudy.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.example.couroutinstudy.R
import com.example.couroutinstudy.model.vo.Alarm

class TestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        requestWindowFeature(Window.FEATURE_NO_TITLE)

        val layoutParam = WindowManager.LayoutParams()

        layoutParam.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND

        layoutParam.dimAmount = 0.7f



    }
}