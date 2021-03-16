package com.example.couroutinstudy.view.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.couroutinstudy.R
import com.example.couroutinstudy.databinding.FragmentDayOfWeekBinding
import com.example.couroutinstudy.view.adapter.DayOfWeekAdapter
import com.example.couroutinstudy.viewmodel.BaseViewModel

class DayOfWeekFrag : Fragment() {

    private var _binding : FragmentDayOfWeekBinding? = null
    private val binding get() =_binding!!
    private lateinit var viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDayOfWeekBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[BaseViewModel::class.java]

        val adapter = DayOfWeekAdapter()
        binding.dayOfweekRv.let{
            it.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
            it.adapter = adapter
        }

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

    }


}