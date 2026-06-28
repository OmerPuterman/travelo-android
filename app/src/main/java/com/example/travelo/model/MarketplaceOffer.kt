package com.example.travelo.model

import com.google.gson.annotations.SerializedName

data class MarketplaceOffer(
    @SerializedName("proposalId")
    val proposalId: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("price")
    val price: Double = 0.0,

    @SerializedName("location")
    val location: String = "",

    @SerializedName("status")
    val status: String = "",
    @SerializedName("lat")
    val lat: Double = 0.0,

    @SerializedName("lon")
    val lng: Double = 0.0,

    @SerializedName("startTime")
    val startTime: String = "",

    @SerializedName("endTime")
    val endTime: String = ""

)