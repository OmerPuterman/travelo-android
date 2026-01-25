package com.example.travelo.ui

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object CreateTrip : Screen("create_trip")
    object TripDetails : Screen("trip_details/{tripId}") { // <--- Updated
        fun createRoute(tripId: String) = "trip_details/$tripId"
    }
}