package com.lexneoapps.unlockpatternapp.ui.checkpassword

import android.annotation.SuppressLint
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.lexneoapps.unlockpatternapp.R
import com.lexneoapps.unlockpatternapp.databinding.FragmentCheckPasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CheckPasswordFragment : Fragment(R.layout.fragment_check_password) {
    private var _binding: FragmentCheckPasswordBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CheckPasswordViewModel by viewModels()

    @Inject
    lateinit var soundPool: SoundPool

    var sound = 0
    var soundId = 0

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
        setSound()
        setUpListeners()
        setUpObservers()

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setUpListeners() {
        binding.tapButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                viewModel.setStartTime()
                soundId = soundPool.play(sound, 0.5F, 0.5F, 0, 10, 1F)
            } else if (event.action == MotionEvent.ACTION_UP) {
                viewModel.setEndTime()
                soundPool.stop(soundId)
            }
            true
        }
        binding.checkButton.setOnClickListener {
            viewModel.checkClick()
        }
        binding.resetButton.setOnClickListener {
            viewModel.restartPassword()
        }
    }

    private fun setUpObservers() {
        viewModel.events.observe(viewLifecycleOwner) {
            when (it) {
                CheckResEnum.EMPTY_PASSWORD -> {
                    makeToast("Password can't be empty")
                }
                CheckResEnum.CORRECT -> {
                    makeToast("Password is correct!")
                    viewModel.setToInitialState()
                    val action =
                        CheckPasswordFragmentDirections.actionCheckPasswordFragmentToSecretFragment()
                    findNavController().navigate(action)
                }
                CheckResEnum.PASSWORD_RESTARTED -> {
                    makeToast("Password has been successfully restarted!")
                    viewModel.setToInitialState()
                    val action =
                        CheckPasswordFragmentDirections.actionCheckPasswordFragmentToStartFragment()
                    findNavController().navigate(action)
                }
                CheckResEnum.PASSWORD_DID_NOT_MATCH ->{
                    makeToast("Password is not correct,try again!")
                }


            }
        }
    }

    private fun setSound() {
        sound = soundPool.load(
            requireContext(),
            R.raw.beep, 1
        )
    }

    private fun makeToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null


    }
}