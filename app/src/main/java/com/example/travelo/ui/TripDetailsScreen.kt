package com.example.travelo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.travelo.model.Proposal
import com.example.travelo.network.RetrofitInstance

@Composable
fun TripDetailsScreen(
    tripId: String,
    onBack: () -> Unit
) {
    var proposals by remember { mutableStateOf<List<Proposal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch proposals when screen loads
    LaunchedEffect(tripId) {
        try {
            val response = RetrofitInstance.api.getProposals(tripId)
            if (response.isSuccessful) {
                proposals = response.body() ?: emptyList()
            }
        } catch (e: Exception) {
            // Handle error silently for MVP
        } finally {
            isLoading = false
        }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text(text = "Trip Details", style = MaterialTheme.typography.headlineMedium)
            Text(text = "ID: $tripId", style = MaterialTheme.typography.bodySmall)

            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Proposals / Route", style = MaterialTheme.typography.titleLarge)

            if (isLoading) {
                CircularProgressIndicator()
            } else if (proposals.isEmpty()) {
                Text("No proposals received yet.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(proposals) { proposal ->
                        ProposalCard(proposal)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}

@Composable
fun ProposalCard(proposal: Proposal) {
    Card(elevation = CardDefaults.cardElevation(4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = proposal.description, style = MaterialTheme.typography.titleMedium)
            Text(text = "Price: $${proposal.price}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Status: ${proposal.status}", style = MaterialTheme.typography.labelSmall)
        }
    }
}