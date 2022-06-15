package com.lexneoapps.unlockpatternapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.lexneoapps.unlockpatternapp.R
import com.lexneoapps.unlockpatternapp.databinding.FragmentCheckPasswordBinding

class CheckPasswordFragment : Fragment(R.layout.fragment_check_password) {
    private var _binding: FragmentCheckPasswordBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckPasswordBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}