package com.example.travelo.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.travelo.model.Trip
import com.example.travelo.network.RetrofitInstance
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTripScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    var dest by remember { mutableStateOf("Paris Group Tour") }
    var budget by remember { mutableStateOf("200") }
    var maxTime by remember { mutableStateOf("480") }
    var startLoc by remember { mutableStateOf("48.8606, 2.3376") }
    var endLoc by remember { mutableStateOf("49.0097, 2.5479") }
    var isLoading by remember { mutableStateOf(false) }

    // Generate a unique 4-digit code for this trip
    val generatedTripCode = remember { "TRIP-${(1000..9999).random()}" }
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Trip Created!") },
            text = { Text("Your unique Trip Code is:\n\n$generatedTripCode\n\nSave this code to manage the trip, and share it with your travelers so they can view the itinerary!") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onBack()
                }) { Text("Got it!") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Plan: $generatedTripCode") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(value = dest, onValueChange = { dest = it }, label = { Text("Destination") }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = budget, onValueChange = { budget = it }, label = { Text("Budget ($)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                OutlinedTextField(value = maxTime, onValueChange = { maxTime = it }, label = { Text("Time (mins)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
            }
            Text("Start & End Coordinates", style = MaterialTheme.typography.labelLarge)
            OutlinedTextField(value = startLoc, onValueChange = { startLoc = it }, label = { Text("Start Loc (Lat, Lon)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = endLoc, onValueChange = { endLoc = it }, label = { Text("End Loc (Lat, Lon)") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isLoading = true
                    val newTrip = Trip(
                        tripId = generatedTripCode,
                        destination = dest,
                        budget = budget.toDoubleOrNull() ?: 500.0,
                        maxTimeMinutes = maxTime.toIntOrNull() ?: 720,
                        startLocation = startLoc,
                        endLocation = endLoc,
                        startDate = "TBD",
                        guideId = "GUIDE",
                        numberOfTravelers = 1 // <-- ADD THIS LINE
                    )

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val response = RetrofitInstance.api.createTrip(newTrip)
                            val responseBody = response.body()?.string() ?: "" // Safely convert to string

                            withContext(Dispatchers.Main) {
                                isLoading = false
                                if (response.isSuccessful) {
                                    showDialog = true // Success!
                                } else {
                                    Toast.makeText(context, "Server Error: ${response.code()}", Toast.LENGTH_LONG).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                Toast.makeText(context, "Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp), enabled = !isLoading
            ) {
                if (isLoading) CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                else Text("Save Trip Constraints")
            }
        }
    }
}