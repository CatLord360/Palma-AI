package com.example.palma.models

import kotlin.collections.List

//START of DATA-CLASS: WeatherResponse
data class WeatherResponse(val weather: List<Weather>, val main: Main, val name: String
)//END of DATA-CLASS: WeatherResponse