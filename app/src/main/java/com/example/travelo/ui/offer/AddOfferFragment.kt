package com.example.travelo.ui.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.travelo.databinding.FragmentAddOfferBinding
import com.example.travelo.network.CreateProposalRequest
import com.example.travelo.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddOfferFragment : Fragment() {

    private var _binding: FragmentAddOfferBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddOfferBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
        binding.submitButton.setOnClickListener { onSubmit() }
    }

    private fun setLoading(loading: Boolean) {
        binding.submitButton.isEnabled = !loading
        binding.loadingIndicator.visibility = if (loading) View.VISIBLE else View.GONE
        binding.submitButton.text = if (loading) "" else "Submit to Marketplace"
    }

    private fun onSubmit() {
        val name = binding.nameInput.text?.toString().orEmpty()
        val cost = binding.costInput.text?.toString().orEmpty()
        val lat = binding.latInput.text?.toString().orEmpty()
        val lon = binding.lonInput.text?.toString().orEmpty()

        if (name.isBlank() || cost.isBlank() || lat.isBlank() || lon.isBlank()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        setLoading(true)

        val request = CreateProposalRequest(
            businessId = "BUSINESS_123",
            tripId = "GLOBAL_MARKETPLACE",
            description = name,
            price = cost.toDoubleOrNull() ?: 0.0,
            location = "$lat,$lon"
        )

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.createProposal(request)
                withContext(Dispatchers.Main) {
                    setLoading(false)
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Offer published!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(requireContext(), "Failed to publish", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    setLoading(false)
                    Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}