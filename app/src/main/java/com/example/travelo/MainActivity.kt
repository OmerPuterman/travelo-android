package com.example.travelo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.travelo.ui.*
import com.example.travelo.ui.theme.TraveloTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TraveloTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {

                        // --- 1. LOGIN ---
                        composable("login") {
                            LoginScreen(
                                onLoginSuccess = { role ->
                                    val dest = when (role) {
                                        "GUIDE" -> "guide_dashboard"
                                        "BUSINESS" -> "business_dashboard"
                                        else -> "traveler_dashboard"
                                    }
                                    navController.navigate(dest) {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- 2. DASHBOARDS ---
                        composable("guide_dashboard") {
                            GuideDashboardScreen(
                                onNavigateToCreate = { navController.navigate("create_trip") },
                                onNavigateToDetails = { tripId -> navController.navigate("route_selection/$tripId") }                            )
                        }

                        composable("business_dashboard") {
                            BusinessDashboardScreen(
                                onNavigateToAddOffer = { navController.navigate("add_offer") },
                                onLogout = {
                                    navController.navigate("login") {
                                        // FIXED: Replaced navController.graph.id with strict String route
                                        popUpTo("business_dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("traveler_dashboard") {
                            TravelerDashboardScreen(
                                onNavigateToItinerary = { tripId -> navController.navigate("traveler_itinerary/$tripId") },
                                onLogout = {
                                    navController.navigate("login") {
                                        // FIXED: Replaced navController.graph.id with strict String route
                                        popUpTo("traveler_dashboard") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // --- 3. SHARED & TRIP FLOWS ---
                        composable("create_trip") {
                            CreateTripScreen(onBack = { navController.popBackStack() })
                        }

                        composable("add_offer") {
                            AddOfferScreen(onBack = { navController.popBackStack() })
                        }

                        composable("route_selection/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: "GLOBAL_MARKETPLACE"
                            RouteSelectionScreen(
                                tripId = tripId,
                                onBack = { navController.popBackStack() },
                                onRouteGenerated = { id ->
                                    navController.navigate("trip_details/$id") {
                                        popUpTo("route_selection/{tripId}") { inclusive = true }
                                    }
                                }
                            )
                        }

                        composable("trip_details/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: "0"
                            TripDetailsScreen(tripId = tripId, onBack = { navController.popBackStack() })
                        }

                        composable("traveler_itinerary/{tripId}") { backStackEntry ->
                            val tripId = backStackEntry.arguments?.getString("tripId") ?: "0"
                            TravelerItineraryScreen(tripId = tripId, onBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}