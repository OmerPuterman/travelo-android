package com.example.travelo.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.travelo.model.Trip
import com.example.travelo.network.RetrofitInstance // <--- Make sure this is here!

@Composable
fun DashboardScreen(
    onCreateTripClick: () -> Unit,
    onTripClick: (String) -> Unit // <--- This was missing!
) {
    var trips by remember { mutableStateOf<List<Trip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitInstance.api.getTrips()
            if (response.isSuccessful) {
                trips = response.body() ?: emptyList()
            } else {
                errorMessage = "Error: ${response.code()}"
            }
        } catch (e: Exception) {
            errorMessage = "Failed: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateTripClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Trip")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(text = "My Trips", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (errorMessage != null) {
                Text(text = errorMessage!!, color = MaterialTheme.colorScheme.error)
            } else if (trips.isEmpty()) {
                Text("No trips found. Click + to create one.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(trips) { trip ->
                        // The clickable box handles the tap
                        Box(modifier = Modifier.clickable { onTripClick(trip.tripId ?: "") }) {
                            TripCard(trip)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TripCard(trip: Trip) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = trip.destination, style = MaterialTheme.typography.titleMedium)
            Text(text = "Date: ${trip.startDate}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Budget: $${trip.budget}", style = MaterialTheme.typography.bodySmall)
        }
    }
}