package com.example.couroutinstudy.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.ActivityDayOfWeekBinding

class DayOfWeekActivity : AppCompatActivity() {

    private lateinit var binding : ActivityDayOfWeekBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDayOfWeekBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}