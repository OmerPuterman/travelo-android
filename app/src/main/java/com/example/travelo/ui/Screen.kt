package com.example.travelo.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object GuideDashboard : Screen("guide_dashboard")
    object BusinessDashboard : Screen("business_dashboard")
    object TravelerDashboard : Screen("traveler_dashboard")
    object CreateTrip : Screen("create_trip")
    object AddOffer : Screen("add_offer")

    // The Mix & Match Screen Route
    object RouteSelection : Screen("route_selection/{tripId}") {
        fun createRoute(tripId: String) = "route_selection/$tripId"
    }

    // The Final Guide Map Route
    object TripDetails : Screen("trip_details/{tripId}") {
        fun createRoute(tripId: String) = "trip_details/$tripId"
    }

    // The Traveler Itinerary Route
    object TravelerItinerary : Screen("traveler_itinerary/{tripId}") {
        fun createRoute(tripId: String) = "traveler_itinerary/$tripId"
    }
}