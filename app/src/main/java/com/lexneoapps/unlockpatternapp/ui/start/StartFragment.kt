package com.lexneoapps.unlockpatternapp.ui.start

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
import com.lexneoapps.unlockpatternapp.databinding.FragmentStartBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StartFragment : Fragment(R.layout.fragment_start) {
    private var _binding: FragmentStartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StartViewModel by viewModels()

    @Inject
    lateinit var soundPool: SoundPool

    var sound = 0
    var soundId = 0


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
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
        binding.saveButton.setOnClickListener {
            viewModel.saveClick()
        }
        binding.changePassword.setOnClickListener {
            viewModel.changePassword()
        }
        binding.showButton.setOnClickListener {
            viewModel.setShowPassword()
        }
    }

    private fun setUpObservers() {
        viewModel.showPassword.observe(viewLifecycleOwner) {
            if (it) {
                binding.showButton.text = getText(R.string.hide_text)
                binding.passwordInputTextView.visibility = View.VISIBLE
                binding.confirmPasswordInputTextView.visibility = View.VISIBLE
            } else {
                binding.showButton.text = getText(R.string.show_text)
                binding.passwordInputTextView.visibility = View.INVISIBLE
                binding.confirmPasswordInputTextView.visibility = View.INVISIBLE
            }
        }
        viewModel.confirmed.observe(viewLifecycleOwner) {
            if (it) {
                binding.saveButton.text = getText(R.string.save_text)
                binding.changePassword.isEnabled = true
            } else {
                binding.saveButton.text = getText(R.string.confirm_text_button)
                binding.changePassword.isEnabled = false
            }
        }
        viewModel.firstPassword.observe(viewLifecycleOwner){
            binding.passwordInputTextView.text = it
        }
        viewModel.secondPassword.observe(viewLifecycleOwner){
            binding.confirmPasswordInputTextView.text = it
        }
        viewModel.events.observe(viewLifecycleOwner){
            when(it){
                StartViewModel.ResEnum.EMPTY_PASSWORD ->{
                    makeToast("Password should not be empty")
                }
                StartViewModel.ResEnum.SAVED -> {
                    makeToast("Password has been saved!")
                    viewModel.setToInitialState()
                    val action = StartFragmentDirections.actionStartFragmentToCheckPasswordFragment()
                    findNavController().navigate(action)
                }
                StartViewModel.ResEnum.PASSWORD_DID_NOT_MATCH ->{
                    makeToast("Password did not match, try again!")
                }
                StartViewModel.ResEnum.PASSWORD_RESTARTED ->{
                    makeToast("Password has been successfully restarted!")
                }
                StartViewModel.ResEnum.PASSWORD_EXISTS ->{
                    viewModel.setToInitialState()
                    val action = StartFragmentDirections.actionStartFragmentToCheckPasswordFragment()
                    findNavController().navigate(action)
                }
            }
        }

    }

    private fun setSound(){
         sound = soundPool.load(
            requireContext(),
            R.raw.beep, 1
        )
    }

    private fun makeToast(msg: String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }
}