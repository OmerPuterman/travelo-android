package com.example.travelo.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
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
fun TravelerItineraryScreen(tripId: String, onBack: () -> Unit) {
    val context = LocalContext.current
    var isAccepted by remember { mutableStateOf(false) }
    var route by remember { mutableStateOf<Route?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch the real generated route from Firebase
    LaunchedEffect(tripId) {
        withContext(Dispatchers.IO) {
            try {
                // Using "GLOBAL_MARKETPLACE" as our current test ID
                val response = RetrofitInstance.api.getRouteForTrip("GLOBAL_MARKETPLACE")
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        route = response.body()
                    }
                    isLoading = false
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    Toast.makeText(context, "Network error fetching itinerary.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Itinerary") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            )
        },
        bottomBar = {
            // Only show the accept button if a route actually exists
            if (route != null) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    PaddingValues(16.dp)
                    Button(
                        onClick = { isAccepted = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(50.dp),
                        enabled = !isAccepted,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        if (isAccepted) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Accepted")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Trip Confirmed!")
                        } else {
                            Text("Accept & Confirm Attendance")
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.tertiary)
                }
            } else if (route == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "Your guide is still planning this trip!\nCheck back later.",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                // Google Maps Button
                // Precision Google Maps Button
                Button(
                    onClick = {
                        val sortedStops = route!!.stops.sortedBy { it.order }
                        if (sortedStops.size >= 2) {
                            val origin = sortedStops.first()
                            val dest = sortedStops.last()

                            // Build a string of waypoints separated by the pipe character '|'
                            val waypoints = sortedStops.drop(1).dropLast(1).joinToString("|") { "${it.lat},${it.lon}" }

                            // Construct the official Google Maps Directions API URL
                            val uriString = "https://www.google.com/maps/dir/?api=1" +
                                    "&origin=${origin.lat},${origin.lon}" +
                                    "&destination=${dest.lat},${dest.lon}" +
                                    (if (waypoints.isNotEmpty()) "&waypoints=$waypoints" else "") +
                                    "&travelmode=walking"

                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uriString))
                            intent.setPackage("com.google.android.apps.maps")
                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Fallback to browser if Maps app isn't installed
                                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uriString)))
                            }
                        } else {
                            Toast.makeText(context, "Not enough stops to map.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("🗺️ Navigate Route on Maps")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // The Live AI Stop List
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(route!!.stops.sortedBy { it.order }) { stop ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Number Badge
                                Surface(
                                    shape = androidx.compose.foundation.shape.CircleShape,
                                    color = MaterialTheme.colorScheme.tertiary,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text("${stop.order}", color = MaterialTheme.colorScheme.onTertiary, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = stop.description,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = stop.arrivalTime,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}