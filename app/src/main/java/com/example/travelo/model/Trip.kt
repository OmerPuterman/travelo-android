package com.example.travelo.model

data class Trip(
    val tripId: String? = null,
    val guideId: String? = null,
    val destination: String,
    val startDate: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val budget: Double,
    val maxTimeMinutes: Int,
    val startLocation: String,
    val endLocation: String,
    val numberOfTravelers: Int = 1
)