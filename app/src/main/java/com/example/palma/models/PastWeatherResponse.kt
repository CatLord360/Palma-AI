package com.example.palma.models

//START of DATA-CLASS: PastWeatherResponse
data class PastWeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val daily: PastWeather
)//END of DATA-CLASS: PastWeatherResponse
