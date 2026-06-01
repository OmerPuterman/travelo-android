package com.example.travelo.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GuideDashboardScreen(onNavigateToCreate: () -> Unit, onNavigateToDetails: (String) -> Unit) {
    var tripCode by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Guide Dashboard", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreate,
                icon = { Icon(Icons.Default.Add, contentDescription = "New Trip") },
                text = { Text("Plan New Trip") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(32.dp))
            Text("Manage an Existing Trip", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = tripCode,
                onValueChange = { tripCode = it.uppercase() },
                label = { Text("Enter Trip Code (e.g. TRIP-1234)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // Force the ID to start with "TRIP-" if the user forgot it
                    val finalCode = if (tripCode.startsWith("TRIP-")) tripCode else "TRIP-$tripCode"
                    if (finalCode.isNotBlank()) onNavigateToDetails(finalCode)
                },                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = tripCode.isNotBlank()
            ) {
                Icon(Icons.Default.Settings, contentDescription = "Manage")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Manage Trip")
            }
        }
    }
}