package com.example.travelo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelerDashboardScreen(onNavigateToItinerary: (String) -> Unit, onLogout: () -> Unit) {
    var tripCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Travels", fontWeight = FontWeight.Bold) },
                actions = { TextButton(onClick = onLogout) { Text("Logout", color = MaterialTheme.colorScheme.error) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Join a Planned Trip", style = MaterialTheme.typography.titleLarge)
            Text("Enter the code provided by your Guide", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = tripCode,
                onValueChange = { tripCode = it.uppercase() },
                label = { Text("Trip Code (e.g. TRIP-1234)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { if (tripCode.isNotBlank()) onNavigateToItinerary(tripCode) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                enabled = tripCode.isNotBlank()
            ) {
                Icon(Icons.Default.Search, contentDescription = "Find")
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Itinerary")
            }
        }
    }
}