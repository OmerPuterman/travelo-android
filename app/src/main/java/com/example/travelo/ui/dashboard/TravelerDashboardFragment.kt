package com.example.travelo.ui.dashboard

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.travelo.R
import com.example.travelo.databinding.FragmentTravelerDashboardBinding

class TravelerDashboardFragment : Fragment() {

    private var _binding: FragmentTravelerDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTravelerDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.logoutButton.setOnClickListener {
            findNavController().navigate(R.id.action_travelerDashboard_to_login)
        }

        binding.tripCodeInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.viewItineraryButton.isEnabled = !s.isNullOrBlank()
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.viewItineraryButton.setOnClickListener {
            val tripCode = binding.tripCodeInput.text?.toString()?.uppercase().orEmpty()
            if (tripCode.isBlank()) return@setOnClickListener
            findNavController().navigate(
                R.id.action_travelerDashboard_to_itinerary,
                bundleOf("tripId" to tripCode)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}