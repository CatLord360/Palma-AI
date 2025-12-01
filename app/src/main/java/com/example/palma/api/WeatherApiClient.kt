package com.example.palma.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//START of OBJECT: WeatherApiClient
object WeatherApiClient {
    val retrofitService: WeatherApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApi::class.java)
    }
}//END of OBJECT: WeatherApiClient