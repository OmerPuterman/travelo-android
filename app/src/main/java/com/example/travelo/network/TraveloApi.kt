package com.example.travelo.network

import com.example.travelo.model.Proposal
import com.example.travelo.model.Trip
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path // <--- This import is critical for @Path

interface TraveloApi {
    // 1. Create a Trip
    @POST("trips")
    suspend fun createTrip(@Body trip: Trip): Response<String>

    // 2. Get All Trips
    @GET("trips")
    suspend fun getTrips(): Response<List<Trip>>

    // 3. Get Proposals (THIS IS WHAT WAS MISSING)
    @GET("proposals/trip/{tripId}")
    suspend fun getProposals(@Path("tripId") tripId: String): Response<List<Proposal>>
}