package com.example.travelo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

// Import your organized packages
import com.example.travelo.network.RetrofitInstance
import com.example.travelo.ui.CreateTripScreen
import com.example.travelo.ui.DashboardScreen
import com.example.travelo.ui.Screen
import com.example.travelo.ui.TripDetailsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // 1. Create the Navigation Controller
            val navController = rememberNavController()

            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {

                    // 2. Setup the NavHost (The map of your app)
                    NavHost(navController = navController, startDestination = Screen.Dashboard.route) {

                        // --- SCREEN 1: DASHBOARD ---
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                onCreateTripClick = {
                                    navController.navigate(Screen.CreateTrip.route)
                                },
                                onTripClick = { tripId ->
                                    // Navigate to details with the ID
                                    navController.navigate(Screen.TripDetails.createRoute(tripId))
                                }
                            )
                        }

                        // --- SCREEN 2: CREATE TRIP ---
                        composable(Screen.CreateTrip.route) {
                            CreateTripScreen(
                                api = RetrofitInstance.api, // Use the singleton we made
                                onTripCreated = {
                                    navController.popBackStack() // Go back to Dashboard
                                },
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        // --- SCREEN 3: TRIP DETAILS ---
                        composable(
                            route = Screen.TripDetails.route,
                            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            // Extract the tripId from the URL
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""

                            TripDetailsScreen(
                                tripId = tripId,
                                onBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}