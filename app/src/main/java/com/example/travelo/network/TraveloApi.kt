package com.example.travelo.network

import com.example.travelo.model.MarketplaceOffer
import com.example.travelo.model.Proposal
import com.example.travelo.model.Route
import com.example.travelo.model.Trip
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class CreateProposalRequest(
    val businessId: String,
    val tripId: String,
    val description: String,
    val price: Double,
    val location: String,
    val status: String = "PENDING"
)

interface TraveloApi {
    @GET("routes/test-algo")
    suspend fun getTestRoute(@Query("roundTrip") roundTrip: Boolean = true): Response<List<Proposal>>

    @GET("trips")
    suspend fun getTrips(): Response<List<Trip>>

    @POST("trips")
    suspend fun createTrip(@Body trip: Trip): Response<okhttp3.ResponseBody>

    @POST("proposals")
    suspend fun createProposal(@Body proposal: CreateProposalRequest): Response<String>

    // --- NEW: Fetch all raw offers for a specific trip ---
    @GET("proposals/trip/{tripId}")
    suspend fun getProposalsForTrip(@Path("tripId") tripId: String): Response<List<MarketplaceOffer>>

    // --- UPDATED: Generate route now accepts a list of checked IDs ---
    @POST("routes/generate/{tripId}")
    suspend fun generateLiveRoute(
        @Path("tripId") tripId: String,
        @Body selectedProposalIds: List<String>
    ): Response<Route>

    // --- NEW: Fetch the finalized AI route ---
    @GET("routes/trip/{tripId}")
    suspend fun getRouteForTrip(@Path("tripId") tripId: String): Response<Route>

    // --- NEW: User Authentication & Role Management ---
    @POST("users/register")
    suspend fun registerUser(@Body user: com.example.travelo.model.User): Response<String>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): Response<com.example.travelo.model.User>
}