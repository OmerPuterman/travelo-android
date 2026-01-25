package com.example.travelo.network


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // 10.0.2.2 is the special IP to reach 'localhost' from the Android Emulator
    private const val BASE_URL = "http://10.0.2.2:8080/api/"

    val api: TraveloApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TraveloApi::class.java)
    }
}