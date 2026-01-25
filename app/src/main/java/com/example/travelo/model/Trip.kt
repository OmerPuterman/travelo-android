package com.example.travelo.model

data class Trip(
    val tripId: String? = null,
    val guideId: String,
    val destination: String,
    val startDate: String,
    val budget: Double,
    val numberOfTravelers: Int
)