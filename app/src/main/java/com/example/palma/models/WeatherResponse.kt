package com.example.palma.models

//START of DATA-CLASS: WeatherResponse
data class WeatherResponse(
    val latitude: Double,
    val longitude: Double,
    val current_weather: Weather
)//END of DATA-CLASS: WeatherResponse
