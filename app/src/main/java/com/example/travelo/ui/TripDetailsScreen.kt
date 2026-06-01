package com.example.travelo.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.travelo.model.Route
import com.example.travelo.network.RetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TripDetailsScreen(tripId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var route by remember { mutableStateOf<Route?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch the generated route from Firebase via Spring Boot
    LaunchedEffect(tripId) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitInstance.api.getRouteForTrip(tripId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        route = response.body()
                    } else {
                        Toast.makeText(context, "No route found. Please generate one first.", Toast.LENGTH_LONG).show()
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    Toast.makeText(context, "Network error.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Final Optimized Itinerary") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (route == null) {
                Text("No itinerary available yet.", style = MaterialTheme.typography.titleMedium)
            } else {
                // Dashboard summary cards
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Cost", style = MaterialTheme.typography.labelMedium)
                            Text("$${route!!.totalCost}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Time", style = MaterialTheme.typography.labelMedium)
                            Text("${route!!.totalTime.toInt()} min", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Basic Google Maps Fallback Hand-off (since we aren't storing coordinates in the Route.Stop model yet)
                Button(
                    onClick = {
                        Toast.makeText(context, "Maps API handoff preparing...", Toast.LENGTH_SHORT).show()
                        val uriString = "https://www.google.com/maps/dir/?api=1?q=Paris"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                        intent.setPackage("com.google.android.apps.maps")
                        try { context.startActivity(intent) } catch (e: Exception) { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uriString))) }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("🗺️ View General Area on Maps")
                }

                Spacer(modifier = Modifier.height(16.dp))

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(route!!.stops.sortedBy { it.order }) { stop ->
                        Card(elevation = CardDefaults.cardElevation(2.dp)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) { Text("${stop.order}", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold) }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(text = stop.description, style = MaterialTheme.typography.titleMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}