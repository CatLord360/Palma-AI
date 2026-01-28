package com.example.palma.models

//START of DATA-CLASS: Weather
data class Weather(
    val temperature: Double,
    val windspeed: Double,
    val winddirection: Double,
    val weathercode: Int,
    val is_day: Int,
    val time: String
)//END of DATA-CLASS: Weather
