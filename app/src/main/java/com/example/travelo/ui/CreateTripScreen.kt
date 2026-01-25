package com.example.travelo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

// IMPORTS needed because we are in a sub-package
import com.example.travelo.model.Trip
import com.example.travelo.network.TraveloApi

@Composable
fun CreateTripScreen(
    api: TraveloApi,
    onTripCreated: () -> Unit,
    onBack: () -> Unit
) {
    // State variables
    var destination by remember { mutableStateOf("") }
    var budget by remember { mutableStateOf("") }
    var travelers by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Plan a New Trip", style = MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = destination,
                onValueChange = { destination = it },
                label = { Text("Destination") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = startDate,
                onValueChange = { startDate = it },
                label = { Text("Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = budget,
                    onValueChange = { budget = it },
                    label = { Text("Budget") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = travelers,
                    onValueChange = { travelers = it },
                    label = { Text("People") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        scope.launch {
                            isLoading = true
                            try {
                                val budgetVal = budget.toDoubleOrNull() ?: 0.0
                                val travelersVal = travelers.toIntOrNull() ?: 1

                                val newTrip = Trip(
                                    guideId = "Guide_App_User", // Hardcoded for now
                                    destination = destination,
                                    startDate = startDate,
                                    budget = budgetVal,
                                    numberOfTravelers = travelersVal
                                )

                                val response = api.createTrip(newTrip)
                                if (response.isSuccessful) {
                                    onTripCreated()
                                } else {
                                    snackbarHostState.showSnackbar("Error: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar("Failed: ${e.message}")
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                ) {
                    Text("Create Trip")
                }

                TextButton(onClick = onBack) {
                    Text("Cancel")
                }
            }
        }
    }
}