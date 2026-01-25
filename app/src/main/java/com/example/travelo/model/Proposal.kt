package com.example.travelo.model

data class Proposal(
    val proposalId: String? = null,
    val tripId: String,
    val businessId: String,
    val description: String,
    val price: Double,
    val location: String,
    val startTime: String,
    val status: String // "PENDING", "ACCEPTED"
)