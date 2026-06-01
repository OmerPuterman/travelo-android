package com.example.travelo.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.travelo.model.MarketplaceOffer
import com.example.travelo.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteSelectionScreen(tripId: String, onBack: () -> Unit, onRouteGenerated: (String) -> Unit) {
    val context = LocalContext.current
    var offers by remember { mutableStateOf<List<MarketplaceOffer>>(emptyList()) }
    val selectedIds = remember { mutableStateListOf<String>() }
    var isLoading by remember { mutableStateOf(true) }
    var isGenerating by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch the available offers when the screen loads
    // Fetch the available offers when the screen loads
    LaunchedEffect(tripId) {
        withContext(Dispatchers.IO) {
            try {
                // CHANGE THIS: Always fetch from GLOBAL_MARKETPLACE
                // regardless of the specific tripId
                val response = RetrofitInstance.api.getProposalsForTrip("GLOBAL_MARKETPLACE")

                // Add this log to verify in Logcat
                Log.d("TRAVELO_TEST", "Marketplace Response: " + response.body().toString())

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        offers = response.body() ?: emptyList()
                        // Pre-select all by default
                        selectedIds.addAll(offers.map { it.proposalId })
                    } else {
                        Log.e("TRAVELO_TEST", "Server Error: ${response.code()}")
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    Log.e("TRAVELO_TEST", "Network Exception: " + e.message)
                    Toast.makeText(context, "Failed to load offers", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Places") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp) {
                Button(
                    onClick = {
                        if (selectedIds.isEmpty()) {
                            Toast.makeText(context, "Select at least one place!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        isGenerating = true
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                // Send the checked IDs to Spring Boot!
                                val response = RetrofitInstance.api.generateLiveRoute(tripId, selectedIds)
                                withContext(Dispatchers.Main) {
                                    isGenerating = false
                                    if (response.isSuccessful && response.body() != null) {
                                        Toast.makeText(context, "Route Optimized!", Toast.LENGTH_SHORT).show()
                                        onRouteGenerated(tripId) // Move to the results screen
                                    } else {
                                        Toast.makeText(context, "Optimization failed. Check constraints.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    isGenerating = false
                                    Toast.makeText(context, "Server Error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(55.dp),
                    enabled = !isGenerating && !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Star, contentDescription = "AI")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generate Route (${selectedIds.size} Selected)")
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            Text("Available Marketplace Offers", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(12.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (offers.isEmpty()) {
                Text("No active offers found in the marketplace.", style = MaterialTheme.typography.bodyLarge)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(offers) { offer ->
                        val isSelected = selectedIds.contains(offer.proposalId)
                        Card(
                            onClick = {
                                if (isSelected) selectedIds.remove(offer.proposalId)
                                else selectedIds.add(offer.proposalId)
                            },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = { checked ->
                                        if (checked) selectedIds.add(offer.proposalId)
                                        else selectedIds.remove(offer.proposalId)
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(offer.description, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text("Cost: $${offer.price}", style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}